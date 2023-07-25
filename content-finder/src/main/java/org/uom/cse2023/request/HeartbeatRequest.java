package org.uom.cse2023.request;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class HeartbeatRequest  extends BaseRequest {

    private InetAddress hearbeatOwnerIP;
    private int hearbearOwnerPort;
    public HeartbeatRequest(InetAddress senderIP, int senderPort) {
        this.type = RequestType.HEARTBEAT;
        this.senderIP = senderIP;
        this.senderPort = senderPort;

        this.message = "".concat(serializationUtil(this.type.name()))
                .concat(serializationUtil(this.senderIP.getHostAddress()))
                .concat(serializationUtil(Integer.toString(this.senderPort)));
    }

    public HeartbeatRequest(String msg) throws UnknownHostException {
        StringTokenizer tokenizer = new StringTokenizer(msg, " ");
        String command = tokenizer.nextToken();
        if (command.equals(RequestType.HEARTBEAT.name())) {
            this.type = RequestType.HEARTBEAT;
            this.hearbeatOwnerIP = InetAddress.getByName(tokenizer.nextToken());
            this.hearbearOwnerPort = Integer.parseInt(tokenizer.nextToken());
        }
    }

    public InetAddress getHearbeatOwnerIP() {
        return hearbeatOwnerIP;
    }

    public int getHearbearOwnerPort() {
        return hearbearOwnerPort;
    }
}
