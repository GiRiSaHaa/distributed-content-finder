package org.uom.cse2023.networkmanager;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteTable {

    //    private final List<Node> neighbourList = Collections.synchronizedList(new ArrayList<>());
    private final List<Node> neighbourList = (new ArrayList<>());

    public synchronized void addNeighbour(Node node) {
        if(Objects.equals(node.getIp().getHostAddress(), NetworkManager.getInstance().getIpAddress().getHostAddress())){
            if (node.port == NetworkManager.getInstance().getPort()) {
                return;
            }
        }

        int currentIndex = -1;
        for (int i = 0; i < neighbourList.size(); i++) {
            if (Objects.equals(neighbourList.get(i).ip.getHostAddress(), node.ip.getHostAddress()) && neighbourList.get(i).port == node.port) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex != -1) {
            return;
        }
        neighbourList.add(node);
    }

    public synchronized void removeNeighbour(Node node) {
        int removeIndex = -1;
        for (int i = 0; i < neighbourList.size(); i++) {
            if (Objects.equals(neighbourList.get(i).ip.getHostAddress(), node.ip.getHostAddress()) && neighbourList.get(i).port == node.port) {
                removeIndex = i;
                break;
            }
        }
        if (removeIndex != -1) {
            neighbourList.remove(removeIndex);
        }
    }

    public List<Node> getNeighbourList() {
        return new ArrayList<>(neighbourList);
    }

    public boolean containsNode(InetAddress inetAddress, int port) {
        for (Node d : neighbourList) {
            if (d.ip.getHostAddress().equalsIgnoreCase(inetAddress.getHostAddress()) && d.port == port) {
                System.out.println("\n\nNode already known: " + d.ip.getHostAddress() + ", " + d.port);
                return true;
            }
        }
        return false;
    }

    public static class Node {

        public boolean primary;
        public InetAddress ip;
        public int port;
        public Timestamp timestamp;

        public Node(boolean primary, InetAddress ip, int port, Timestamp timestamp) {
            this.primary = primary;
            this.ip = ip;
            this.port = port;
            this.timestamp = timestamp;
        }

        public Node(boolean primary, InetAddress ip, int port) {
            this.primary = primary;
            this.ip = ip;
            this.port = port;
            this.timestamp = new Timestamp(System.currentTimeMillis() + 50000);
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public InetAddress getIp() {
            return ip;
        }

        public void setIp(InetAddress ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }

}
