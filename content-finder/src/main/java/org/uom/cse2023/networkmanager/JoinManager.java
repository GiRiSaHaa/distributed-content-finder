package org.uom.cse2023.networkmanager;

import org.uom.cse2023.request.JoinRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class JoinManager {
    private final BlockingQueue<JoinRequest> joinRequestQueue = new LinkedBlockingDeque<>();
    private final HashMap<UUID, Instant> receivedList = new HashMap<>();
    private final HashMap<UUID, JoinRequest> sentList = new HashMap<>();

    public void start() {
        this.handleJoinRequest();
        this.cleanUpReceivedRequestList();
    }

    public boolean sendJoinRequest(RouteTable.Node node) {
        // return if node is self.
        if(Objects.equals(node.getIp().getHostAddress(), NetworkManager.getInstance().getIpAddress().getHostAddress())){
            if (node.port == NetworkManager.getInstance().getPort()) {
                return false;
            }
        }

        // return if node already joined
        if (NetworkManager.getInstance().getRouteTable().containsNode(node.getIp(), node.getPort())){
            return false;
        }

        JoinRequest req = new JoinRequest();
        req.setNewMemberIP(node.ip);
        req.setNewMemberPort(node.port);
        this.sentList.put(req.getIdentifier(), req);


        NetworkManager.getInstance().sendMessages(
                req,
                node.ip,
                node.port
        );
        return true;
    }


    /**
     * Method for join request receiver (JOIN) to save neighbour
     * and reply back.
     * @param incomingRequest the Incoming Request
     */

    public void replyJoinRequest(JoinRequest incomingRequest) {
        if (receivedList.containsKey(incomingRequest.getIdentifier())) {
            return;
        }

        receivedList.put(incomingRequest.getIdentifier(), Instant.now());

        // add neighbour
        NetworkManager.getInstance().getRouteTable().addNeighbour(new RouteTable.Node(
                false,
                incomingRequest.getNewMemberIP(),
                incomingRequest.getNewMemberPort()));

        JoinRequest response = new JoinRequest(0, incomingRequest.getIdentifier());

        // send back reply JOINOK
        NetworkManager.getInstance().sendMessages(
                response,
                incomingRequest.getNewMemberIP(),
                incomingRequest.getNewMemberPort()
        );
    }


    /**
     * Method for join response receiver to save neighbour
     * @param incomingResponse the Incoming Response
     */
    public void handleJoinResponse(JoinRequest incomingResponse){
        if (!sentList.containsKey(incomingResponse.getSenderIdentifier())){
            return;
        }

        // we sent the message, so we should have its identifier.
        JoinRequest originalJoinRequest = sentList.get(incomingResponse.getSenderIdentifier());

        NetworkManager.getInstance().getRouteTable().addNeighbour(new RouteTable.Node(
                false,
                originalJoinRequest.getNewMemberIP(),
                originalJoinRequest.getNewMemberPort()));

        sentList.remove(originalJoinRequest.getSenderIdentifier());
    }

    public void handleJoinRequest() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while (true) {
                    try {
                        JoinRequest joinRequest = joinRequestQueue.take(); // retrieve and remove
                        switch (joinRequest.getType()) {
                            case JOIN:
                                replyJoinRequest(joinRequest);
                                break;
                            case JOINOK:
                                handleJoinResponse(joinRequest);
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 250);
    }


    public void cleanUpReceivedRequestList() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Iterator receivedListIterator = receivedList.entrySet().iterator();

                while (receivedListIterator.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) receivedListIterator.next();
                    if (Duration.between((Instant) pair.getValue(), Instant.now()).getSeconds() > 120) {
                        receivedListIterator.remove(); // remove last returned pair
                    }
                }
            }
        }, 0, 500);
    }


    void addJoinRequestToQueue(JoinRequest req) {
        try {
            this.joinRequestQueue.put(req);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
