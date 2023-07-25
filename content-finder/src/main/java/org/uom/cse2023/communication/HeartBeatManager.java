package org.uom.cse2023.communication;

import org.apache.log4j.Logger;
import org.uom.cse2023.networkmanager.NetworkManager;
import org.uom.cse2023.networkmanager.RouteTable;
import org.uom.cse2023.request.BaseRequest;
import org.uom.cse2023.request.HeartbeatRequest;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class HeartBeatManager {
    private final static int PERIOD = 5000;
    private final static int NODE_EXPIRE = PERIOD * 2;
    private final static Logger logger = Logger.getLogger(HeartBeatManager.class);
    private final BlockingQueue<HeartbeatRequest> heartbeatQueue = new LinkedBlockingDeque<>();

    public void start() {
        sendHeartbeat();
        handleHeartbeat();
    }

    public void sendHeartbeat() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (RouteTable.Node node : NetworkManager.getInstance().getRouteTable().getNeighbourList()) {
                    BaseRequest hb = new HeartbeatRequest(NetworkManager.getInstance().getIpAddress(),
                            NetworkManager.getInstance().getPort());
                    NetworkManager.getInstance().sendMessages(hb, node.ip, node.port);
                }
            }
        }, 0, PERIOD);
    }

    private void handleHeartbeat() {
        // Remove the nodes which have not sent the last HB within the declared period
        List<RouteTable.Node> removingNodes = new ArrayList<>();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (RouteTable.Node node : NetworkManager.getInstance().getRouteTable().getNeighbourList()) {
                    if ((node.getTimestamp().getTime() + NODE_EXPIRE) < System.currentTimeMillis()) {
                        removingNodes.add(node);
                    }
                }

                for (RouteTable.Node node : removingNodes) {
                    NetworkManager.getInstance().getRouteTable().removeNeighbour(node);
                    logger.info("Removed the node: " + node.getIp() + " : " + node.port
                            + " From the routing table of node: " + NetworkManager.getInstance().getIpPort() + "Current Timestamp - " + System.currentTimeMillis() + "  Node's Old Timestamp " + node.getTimestamp().getTime());
                }

                removingNodes.clear();

            }
        }, 0, NODE_EXPIRE);

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                for (Iterator<RouteTable.Node> it = NetworkManager.getInstance().getRouteTable().getNeighbourList().iterator(); it.hasNext(); ) {
//                    RouteTable.Node node = it.next();
//                    if ((node.getTimestamp().getTime() + NODE_EXPIRE) < System.currentTimeMillis()) {
//                        it.remove();
//                        logger.info("Current Timestamp - " + System.currentTimeMillis() + " Node's Old Timestamp" + node.getTimestamp().getTime() + "Removed the node: " + node.getIp() + " : " + node.port
//                                + " From the routing table of node: " + NetworkManager.getInstance().getIpPort());
//                    }
//                }
//
//            }
//        }, 0, NODE_EXPIRE);

        // Update the timestamp
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while (true) {
                    try {
                        HeartbeatRequest hbRequest = heartbeatQueue.take();
                        Optional<RouteTable.Node> node = NetworkManager.getInstance().getRouteTable().getNeighbourList()
                                .stream()
                                .filter(n -> n.getIp().getHostAddress().equals(hbRequest.getHearbeatOwnerIP().getHostAddress())
                                        && n.getPort() == hbRequest.getHearbearOwnerPort())
                                .findFirst();

                        if (node.isPresent()) {
                            RouteTable.Node n = node.get();
                            n.setTimestamp(new Timestamp(System.currentTimeMillis()));
                            logger.info("Updated timestamp of the node: " + n.getIp().getHostAddress() + " : " + n.port
                                    + " In the routing table of node: " + NetworkManager.getInstance().getIpPort());
                        }

                    } catch (InterruptedException e) {
                        logger.error("Exception occurred while adding HB message to the queue", e);
                    }

                    //TODO if a node is sending hb messages to you which is not in your routing table notify that particular node
                }
            }
        }, 0, PERIOD);
    }

    public void queueHBMessage(HeartbeatRequest hbMsg) {
        try {
            heartbeatQueue.put(hbMsg);

        } catch (InterruptedException e) {
            logger.error("Exception occurred while adding HB message to the queue", e);
        }
    }
}
