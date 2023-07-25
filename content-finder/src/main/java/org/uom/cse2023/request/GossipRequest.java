package org.uom.cse2023.request;

import org.uom.cse2023.networkmanager.RouteTable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class GossipRequest extends BaseRequest {

    private final List<RouteTable.Node> neighbourList = new ArrayList<>();

    /**
     * Constructor to create outbound Gossip request.
     *
     * @param nodeList the Node List
     */
    public GossipRequest(RequestType type, List<RouteTable.Node> nodeList) {
        this.type = type;

        String nodeMsg = nodeList
                .stream()
                .map(node -> node.ip + " " + node.port)
                .collect(Collectors.joining(" "));

        this.message = "".concat(serializationUtil(this.type.name()))
                .concat(serializationUtil(this.senderIP.getHostAddress()))
                .concat(serializationUtil(Integer.toString(this.senderPort)))
                .concat(serializationUtil(nodeMsg));
    }

    /**
     * Constructor to recreate Gossip request from incoming request.
     *
     * @param msg the message
     * @throws UnknownHostException the Exception
     */
    public GossipRequest(String msg) throws UnknownHostException {
        StringTokenizer tokenizer = new StringTokenizer(msg, " ");

        this.type = RequestType.valueOf(tokenizer.nextToken().toUpperCase());

        this.senderIP = InetAddress.getByName(tokenizer.nextToken());
        this.senderPort = Integer.parseInt(tokenizer.nextToken());

        while (tokenizer.hasMoreElements()) {
            InetAddress ip = InetAddress.getByName(tokenizer.nextToken());
            int port = Integer.parseInt(tokenizer.nextToken());
            neighbourList.add(new RouteTable.Node(false, ip, port));
        }
    }

    public List<RouteTable.Node> getNeighbourList() {
        return neighbourList;
    }

}
