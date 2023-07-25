package org.uom.cse2023.networkmanager;

import org.uom.cse2023.request.BaseRequest;
import org.uom.cse2023.request.BootstrapServerRequest;
import org.uom.cse2023.request.LeaveRequest;
import org.uom.cse2023.request.RequestType;

import java.net.InetAddress;

public class BootstrapManger {
    public void connectBootstrapServer(InetAddress bsIP, int bsPort) {
        BootstrapServerRequest bootstrapServerRequest = new BootstrapServerRequest(
                RequestType.REG
        );
        NetworkManager.getInstance().sendMessages(
                bootstrapServerRequest,
                bsIP,
                bsPort
        );
    }

    public void disconnectBootstrapServer(InetAddress bsIP, int bsPort) {
        BootstrapServerRequest bootstrapServerRequest = new BootstrapServerRequest(
                RequestType.UNREG
        );

        NetworkManager.getInstance().sendMessages(
                bootstrapServerRequest,
                bsIP,
                bsPort
        );
    }

    public void joinNetwork() {
//        JoinRequest joinRequest = new JoinRequest();
        for (RouteTable.Node node: NetworkManager.getInstance().getRouteTable().getNeighbourList()){
            NetworkManager.getInstance().getJoinManager().sendJoinRequest(node);
        }
    }

    public void leaveNetwork() {
        LeaveRequest leaveRequest = new LeaveRequest();
        for (RouteTable.Node node: NetworkManager.getInstance().getRouteTable().getNeighbourList()){
            NetworkManager.getInstance().sendMessages(leaveRequest, node.ip, node.port);
        }
        NetworkManager.getInstance().resetInstance();
    }

    public void handleConnectResponse(BaseRequest request) {
        if (request.getType() ==
                RequestType.REGOK) {
            BootstrapServerRequest bootstrapServerRequest = (BootstrapServerRequest)request;
            for (RouteTable.Node node: bootstrapServerRequest.getNeighbourList()) {
//                NetworkManager.getInstance().getRouteTable().addNeighbour(node);
                NetworkManager.getInstance().getJoinManager().sendJoinRequest(node);
            }

            this.joinNetwork();

        } else if (request.getType() ==
                RequestType.UNROK) {
            this.leaveNetwork();
        } else if(request.getType() == RequestType.LEAVE) {
            LeaveRequest leaveRequest = (LeaveRequest) request;
            NetworkManager.getInstance().getRouteTable().removeNeighbour(new RouteTable.Node(
                    false,
                    leaveRequest.getLeavingMemberIP(),
                    leaveRequest.getLeavingMemberPort()));

        } else if(request.getType() == RequestType.LEAVEOK) {
            System.out.println("LEAVED");
        }

    }
}
