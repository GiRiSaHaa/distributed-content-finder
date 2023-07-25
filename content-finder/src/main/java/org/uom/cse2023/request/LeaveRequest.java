package org.uom.cse2023.request;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class LeaveRequest extends BaseRequest {

    InetAddress leavingMemberIP;
    int leavingMemberPort;

    public LeaveRequest() {
        this.type = RequestType.LEAVE;
        this.message="".concat(serializationUtil(this.type.name()))
                .concat(serializationUtil(this.senderIP.getHostAddress()))
                .concat(serializationUtil(Integer.toString(this.senderPort)));
    }

    public LeaveRequest(int value) {
        this.type = RequestType.LEAVEOK;
        this.message.concat(serializationUtil(this.type.name()))
                .concat(String.valueOf(value));
    }

    public LeaveRequest(String msg) throws UnknownHostException {
        StringTokenizer tokenizer = new StringTokenizer(msg, " ");
        String command = tokenizer.nextToken();
        if (command.equals(RequestType.LEAVE.name())) {
            this.type = RequestType.LEAVE;
            this.leavingMemberIP = InetAddress.getByName(tokenizer.nextToken());
            this.leavingMemberPort = Integer.parseInt(tokenizer.nextToken());
        } else if (command.equals(RequestType.LEAVEOK.name())) {
            int value = Integer.parseInt(tokenizer.nextToken());
            if (value == 0) {
                this.type = RequestType.LEAVEOK;
            } else if (value == 9999) {
                this.type = RequestType.ERROR;
            }
        }
    }


    public InetAddress getLeavingMemberIP() {
        return leavingMemberIP;
    }

    public int getLeavingMemberPort() {
        return leavingMemberPort;
    }
}
