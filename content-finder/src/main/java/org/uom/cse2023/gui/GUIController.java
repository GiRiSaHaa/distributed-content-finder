package org.uom.cse2023.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.uom.cse2023.file.FileClient;
import org.uom.cse2023.file.FileManager;
import org.uom.cse2023.networkmanager.IContentSearch;
import org.uom.cse2023.networkmanager.NetworkManager;
import org.uom.cse2023.networkmanager.RouteTable;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class GUIController implements Initializable, IContentSearch {

    @FXML
    private TextArea runningLog;

    @FXML
    private TextArea searchInput;

    @FXML
    private Button searchButton;

    @FXML
    private Button downloadButton;

    @FXML
    private Button showNeighboursButton;

    @FXML
    private Button showFilesButton;

    @FXML
    private ListView<String> searchResultsList;

    @FXML
    private ListView<String> neighboursList;

    @FXML
    private ListView<String> myFilesList;

    @FXML
    private Button setIP;

    @FXML
    private TextField ipInput;

    @FXML
    private Label ipPortOutput;

    private boolean ready;
    private String selectedFile;
    private SearchResult result;
    private HashMap<String, String> receivedFileList = new HashMap<>();

    @FXML
    void search(ActionEvent event) {

        System.out.println("Search for");
        String search = searchInput.getText();
        System.out.println(search);
        NetworkManager.getInstance().search(search, GUIController.this);

        if (receivedFileList != null){
            receivedFileList.clear();
        }
        updateSearchResults();
    }

    @FXML
    void showNeighbours(ActionEvent event) {
        ObservableList<String> items = FXCollections.observableArrayList();
        int count = 1;
        for (RouteTable.Node node: NetworkManager.getInstance().getRouteTable().getNeighbourList()){
            items.add(count + " " + node.getIp().getHostAddress() + " " + node.getPort());
            count++;
        }

        neighboursList.setItems(items);
    }

    @FXML
    void showFiles(ActionEvent event){
        ObservableList<String> files = FXCollections.observableArrayList();
        for (String file : FileManager.getIntance().getMyFiles()) {
            files.add(file);
        }

        myFilesList.setItems(files);
    }

    @FXML
    void downloadFile(ActionEvent event){
        boolean download = FileClient.download(result.ip.getHostAddress(), result.port, result.fileName);
        System.out.println("Downloaded = " + download);
    }

    @FXML
    void setIP(ActionEvent event){
        String IP = ipInput.getText();

        NetworkManager.getInstance(IP).start();

        ipPortOutput.setText(NetworkManager.getInstance().getIpPort());

        setIP.setDisable(true);
        showFilesButton.setDisable(false);
        showNeighboursButton.setDisable(false);
        searchButton.setDisable(false);

    }

    @FXML
    public void handleMouseClick(MouseEvent arg0) throws UnknownHostException {
        String item = searchResultsList.getSelectionModel().getSelectedItem().split(" -> ")[1];
        selectedFile = receivedFileList.get(item);
        result = new SearchResult(item, InetAddress.getByName(receivedFileList.get(item).split(" ")[0].substring(1)),
                Integer.parseInt(receivedFileList.get(item).split(" ")[1]));
        downloadButton.setDisable(false);
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFilesButton.setDisable(true);
        showNeighboursButton.setDisable(true);
        searchButton.setDisable(true);
        downloadButton.setDisable(true);
    }

    @Override
    public void onSearchResults(InetAddress ownerAddress, int port, List<String> files) {

        System.out.println("Files received");
        for (String file : files) {
            System.out.println(file + " ---- " + port);
            receivedFileList.put(file, ownerAddress + " " + port);
        }

        updateSearchResults();
    }

    private void updateSearchResults(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> itemList = new ArrayList<>();
                for (String file : receivedFileList.keySet()){
                    itemList.add(receivedFileList.get(file).substring(1) + " -> " + file);
                }
                ObservableList<String> items = FXCollections.observableArrayList (itemList);
                searchResultsList.setItems(items);
            }
        });

    }


    private static class SearchResult {
        String fileName;
        InetAddress ip;
        int port;

        public SearchResult(String fileName, InetAddress ip, int port) {
            this.fileName = fileName;
            this.ip = ip;
            this.port = port;
        }
    }

}
