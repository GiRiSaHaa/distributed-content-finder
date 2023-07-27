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
    public Button joinButton;
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
    private Button join;

    @FXML
    private TextField ipInput;

    @FXML
    private TextField nameInput;

    @FXML
    private Label ipPortOutput;

    private boolean ready;
    private String selectedFile;
    private SearchResult result;
    private HashMap<String, String> receivedFileList = new HashMap<>();

    @FXML
    void search(ActionEvent event) {

        System.out.print("Searching for : ");
        String search = searchInput.getText();
        System.out.println(search);
        NetworkManager.getInstance().search(search, GUIController.this);

//        if (receivedFileList != null){
//            receivedFileList.clear();
//        }
        updateSearchResults();
    }

    @FXML
    void showNeighbours(ActionEvent event) {
        ObservableList<String> items = FXCollections.observableArrayList();
        int count = 1;
        items.add("#    IP Address          PORT" );
        for (RouteTable.Node node: NetworkManager.getInstance().getRouteTable().getNeighbourList()){
            items.add(count + "     " + node.getIp().getHostAddress() + "            " + node.getPort());
            count++;
        }

        neighboursList.setItems(items);
    }

    @FXML
    void downloadFile(ActionEvent event){
        boolean download = FileClient.download(result.ip.getHostAddress(), result.port, result.fileName);
        System.out.println("Downloaded = " + download);
    }

    @FXML
    void join(ActionEvent event){
        NetworkManager.getInstance(ipInput.getText(), nameInput.getText()).start();

        ipPortOutput.setText("Connected : " + NetworkManager.getInstance().getIpPort());

        join.setDisable(true);
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
        showNeighboursButton.setDisable(true);
        searchButton.setDisable(true);
        downloadButton.setDisable(true);

        ObservableList<String> files = FXCollections.observableArrayList();
        files.addAll(FileManager.getIntance().getMyFiles());

        myFilesList.setItems(files);
    }

    @Override
    public void onSearchResults(InetAddress ownerAddress, int port, List<String> files) {

        System.out.println("Content Received");
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
