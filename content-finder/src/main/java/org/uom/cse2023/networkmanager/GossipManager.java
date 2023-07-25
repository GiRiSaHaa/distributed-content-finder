package org.uom.cse2023.networkmanager;

import org.uom.cse2023.request.GossipRequest;
import org.uom.cse2023.request.RequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class GossipManager {
    private final static int NEIGHBOUR_LIMIT = 3;
    private final static int GOSSIP_PERIOD = 5000;
    private final BlockingQueue<GossipRequest> gossipRequestQueue = new LinkedBlockingDeque<>();

    void start() {
        this.handleGossipRequests();
        this.sendGossipRequests();
    }

    private synchronized void processGossipReply(GossipRequest gossipRequest) {
        int count = NEIGHBOUR_LIMIT-NetworkManager.getInstance().getRouteTable().getNeighbourList().size();
        List<RouteTable.Node> newNodeList = gossipRequest.getNeighbourList().stream()
                .filter(neighbour -> !NetworkManager.getInstance()
                        .getRouteTable().containsNode(neighbour.ip, neighbour.port))
                .collect(Collectors.toList());

        for (RouteTable.Node node : newNodeList) {
            if (count<=0) break;
//            System.out.println("**********Count***********");
//            System.out.println("**********Count***********");
//            System.out.println("**********Count***********");
//            System.out.println("**********Count***********");
//            System.out.println(count);
            boolean flag = NetworkManager.getInstance().getJoinManager().sendJoinRequest(node);
            if (flag)
                count -=1;

        }

    }

    private void replyGossipRequest(GossipRequest gossipRequest) {
//        processGossipReply(gossipRequest); //to add nodes in gossip request to route table //Todo is this needed?
        List<RouteTable.Node> nodesList = new ArrayList<>(NetworkManager.getInstance().getRouteTable()
                .getNeighbourList());
        GossipRequest replyGossipRequest = new GossipRequest(RequestType.GOSSIPOK,
                nodesList);
        NetworkManager.getInstance().sendMessages(replyGossipRequest,
                gossipRequest.getSenderIP(), gossipRequest.getSenderPort());
    }


    private void handleGossipRequests() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while (gossipRequestQueue.size() > 0) {
                    try {
                        GossipRequest gossipRequest = gossipRequestQueue.take();
                        if (gossipRequest.getType() == RequestType.GOSSIPOK) {
                            processGossipReply(gossipRequest);
                        } else if (gossipRequest.getType() == RequestType.GOSSIP) {
                            replyGossipRequest(gossipRequest);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, GOSSIP_PERIOD);
    }

    private void sendGossipRequests() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (NetworkManager.getInstance().getRouteTable().getNeighbourList().size() < NEIGHBOUR_LIMIT) {
                    List<RouteTable.Node> nodesList = new ArrayList<>(NetworkManager.getInstance().getRouteTable()
                            .getNeighbourList());
                    for (RouteTable.Node node : NetworkManager.getInstance().getRouteTable().getNeighbourList()) {
                        GossipRequest gossipRequest = new GossipRequest(RequestType.GOSSIP,
                                nodesList);
                        NetworkManager.getInstance().sendMessages(gossipRequest, node.ip, node.port);
                    }
                }
            }
        }, 0, GOSSIP_PERIOD);
    }

    void addGossipRequest(GossipRequest gossipRequest) {
        try {
            gossipRequestQueue.put(gossipRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
