package org.uom.cse2023.networkmanager;

import java.net.InetAddress;
import java.util.List;

public interface IContentSearch {
    void onSearchResults(InetAddress ownerAddress, int port, List<String> files);
}
