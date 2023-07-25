package org.uom.cse2023.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.uom.cse2023.networkmanager.NetworkManager;

import java.io.IOException;
import java.util.Objects;

public class GUI extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("GUI.fxml")));

        Scene scene = new Scene(root);

//        PrintStream ps = new PrintStream( taos );
//        System.setOut( ps );
//        System.setErr( ps );

        primaryStage.setScene(scene);
        primaryStage.setTitle("Distributed Search App");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            if (NetworkManager.getInstance() != null){
                NetworkManager.getInstance().stop();
            }
        });
    }

}
