package org.uom.cse2023.networkmanager;

import org.uom.cse2023.file.FileManager;
import org.uom.cse2023.request.RequestType;
import org.uom.cse2023.request.SearchRequest;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchManager {
    private static final int SEARCH_HANDLE_PERIOD = 400;
    private final BlockingQueue<SearchRequest> searchRequestQueue = new LinkedBlockingQueue<>();
    private final Map<String, IContentSearch> searchQueries = new ConcurrentHashMap<>();
    private final ArrayList<UUID> receivedList = new ArrayList<>();
    void start() {
        this.handleSearchRequest();
    }

    public void sendSearchRequest(String searchName, IContentSearch app) {
        SearchRequest request = new SearchRequest(
                RequestType.SER,
                searchName
        );
        searchQueries.put(request.getIdentifier().toString(), app);
        for (RouteTable.Node neighbour: NetworkManager.getInstance().getRouteTable().getNeighbourList()) {
            NetworkManager.getInstance().sendMessages(
                    request,
                    neighbour.ip,
                    neighbour.port
            );
        }
    }

    private void processSearchReply(SearchRequest searchRequest) {
        String searchName = searchRequest.getSearchName();
        String identifier = searchRequest.getIdentifier().toString();
        List<String> files = searchRequest.getFileNames();
        InetAddress fileOwner = searchRequest.getResultOwnerIP();
        int port = searchRequest.getResultOwnerPort();
        IContentSearch searchApp = searchQueries.get(identifier);
        searchApp.onSearchResults(fileOwner, port, files);
    }

    private void replySearchRequest(SearchRequest searchRequest) {
        if (receivedList.contains(searchRequest.getIdentifier())) {
            return;
        }

        receivedList.add(searchRequest.getIdentifier());
        String searchName = searchRequest.getSearchName();
        List<String> matchingFiles = FileManager.getIntance().getMatchingFiles(searchName);
        if (matchingFiles.size() > 0) {
            SearchRequest replyRequest = new SearchRequest(
                    RequestType.SEROK,
                    matchingFiles,
                    searchRequest.getHopsCount() + 1,
                    searchRequest.getIdentifier()
            );
            NetworkManager.getInstance().sendMessages(replyRequest,
                    searchRequest.getSearcherIP(),
                    searchRequest.getSearcherPort());
        } else {
            //Todo only if files are not found, the request is forwarded  ???
            searchRequest.prepareForward();
            for (RouteTable.Node node: NetworkManager.getInstance().getRouteTable().getNeighbourList()) {
                if (searchRequest.getSearcherIP().getHostAddress().equals(node.getIp().getHostAddress()) &&
                        searchRequest.getSearcherPort() == node.getPort()) {
                    continue;
                }
                NetworkManager.getInstance().sendMessages(searchRequest, node.ip, node.port);
            }
        }
    }

    private void handleSearchRequest() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while (searchRequestQueue.size()>0) {
                    try {
                        SearchRequest searchRequest = searchRequestQueue.take();
                        switch (searchRequest.getType()) {
                            case SER:
                                replySearchRequest(searchRequest);
                                break;
                            case SEROK:
                                processSearchReply(searchRequest);
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, SEARCH_HANDLE_PERIOD);

    }

    void addSearchRequest(SearchRequest searchRequest) {
        try {
            searchRequestQueue.put(searchRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
