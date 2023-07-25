package org.uom.cse2023.request;

import org.uom.cse2023.networkmanager.NetworkManager;
import org.uom.cse2023.networkmanager.RouteTable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class BootstrapServerRequest extends BaseRequest{

    private final List<RouteTable.Node> neighbourList = new ArrayList<>();

    public BootstrapServerRequest(RequestType type) {
        this.type = type;
        if (type == RequestType.REG) {
//            this.message.concat(this.type.name())
//                    .concat(this.senderIP.getHostAddress())
//                    .concat(String.valueOf(this.senderPort))
//                    .concat(NetworkManager.getInstance().getUserName());

            message = String.format(" REG %s %d %s", this.senderIP.getHostAddress(), this.senderPort,
                    NetworkManager.getInstance().getUserName());
        } else if (type == RequestType.UNREG) {
            this.message="".concat(serializationUtil(this.type.name()))
                    .concat(serializationUtil(this.senderIP.getHostAddress()))
                    .concat(serializationUtil(String.valueOf(this.senderPort)))
                    .concat(serializationUtil(NetworkManager.getInstance().getUserName()));
//            message = String.format("UNREG %s %d %s", this.senderIP, this.senderPort,
//                    NetworkManager.getInstance().getUserName());
        } else {
            throw new RuntimeException("Invalid request type"); // Todo replace with custom exception
        }


    }

    public BootstrapServerRequest(String incomingMessage) {
        StringTokenizer tokens = new StringTokenizer(incomingMessage, " ");
        String command = tokens.nextToken();

        if (command.equals(RequestType.REGOK.name())) {
            this.type = RequestType.REGOK;
            int nodes = Integer.parseInt(tokens.nextToken());
            for (int i = 0; i < nodes; i++) {
                try {
                    InetAddress neighbourAddress = InetAddress.getByName(tokens.nextToken());
                    int neighbourPort = Integer.parseInt(tokens.nextToken());
                    neighbourList.add(new RouteTable.Node(true, neighbourAddress, neighbourPort));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        } else if(command.equals(RequestType.UNROK.name())) {
            int value = Integer.parseInt(tokens.nextToken());
            if (value == 0) {
                this.type = RequestType.UNROK;
            } else {
                this.type = RequestType.ERROR;
            }
        }
    }

    public List<RouteTable.Node> getNeighbourList() {
        return neighbourList;
    }
}
