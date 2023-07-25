package org.uom.cse2023.request;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.UUID;

public class JoinRequest extends BaseRequest {

    InetAddress newMemberIP;
    int newMemberPort;
    private UUID identifier;
    private UUID senderIdentifier;

    public JoinRequest() {
        this.type = RequestType.JOIN;
        this.identifier = UUID.randomUUID();

        this.message = String.format(" JOIN %s %d %s", senderIP.getHostAddress(), senderPort, identifier.toString());
    }

    public JoinRequest(int errorValue, UUID identifier) {
        this.type = RequestType.JOINOK;
        this.message = String.format(" JOINOK %d %s", errorValue, identifier.toString());
    }

    public JoinRequest(String msg) throws UnknownHostException {
        StringTokenizer tokenizer = new StringTokenizer(msg, " ");
        String command = tokenizer.nextToken();
        if (command.equals(RequestType.JOIN.name())) {
            this.type = RequestType.JOIN;
            this.newMemberIP = InetAddress.getByName(tokenizer.nextToken());
            this.newMemberPort = Integer.parseInt(tokenizer.nextToken());
            this.identifier = UUID.fromString(tokenizer.nextToken());
        } else if (command.equals(RequestType.JOINOK.name())) {
            int value = Integer.parseInt(tokenizer.nextToken());
            if (value == 0) {
                this.type = RequestType.JOINOK;
            } else if (value == 9999) {
                this.type = RequestType.ERROR;
            }
            this.senderIdentifier = UUID.fromString(tokenizer.nextToken());
        }

    }

    public InetAddress getNewMemberIP() {
        return newMemberIP;
    }

    public void setNewMemberIP(InetAddress IP){
        this.newMemberIP = IP;
    }

    public int getNewMemberPort() {
        return newMemberPort;
    }

    public void setNewMemberPort(int port){
        this.newMemberPort = port;
    }

    public UUID getIdentifier() { return this.identifier; }

    /**
     * Method to identify who sent the original JOIN request
     * to handle the JOINOK response.
     *
     * @return the UUID
     */
    public UUID getSenderIdentifier() { return this.senderIdentifier; }
}
