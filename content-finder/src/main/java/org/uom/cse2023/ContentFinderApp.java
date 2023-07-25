package org.uom.cse2023;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.uom.cse2023.gui.GUI;
import org.uom.cse2023.networkmanager.NetworkManager;

import java.util.Random;

@SpringBootApplication
public class ContentFinderApp {

    private static final Logger logger = Logger.getLogger(ContentFinderApp.class);

    public static void main(String[] args) {
        int springPort = new Random().nextInt(10000) + 1200; // ports above 1200;
        logger.info("Initializing the Spring-Boot server on port:" + springPort);
        SpringApplication.run(ContentFinderApp.class, "--server.port=" + springPort);
        NetworkManager.setSearchPort(springPort);
        GUI.main(args);
    }
}