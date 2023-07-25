package org.uom.cse2023.file;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;

public class FileClient {
    private static final Logger logger = Logger.getLogger(FileClient.class);
    private static final String PATH = "/download";

    private FileClient() {
    }

    public static boolean download(String ipAddr, int port, String fileName) {
        if (Arrays.asList(FileManager.getFiles()).contains(fileName)) {
            File file = new File(fileName.replace("-", " "));

            CloseableHttpClient client = HttpClients.createDefault();
            try (CloseableHttpResponse response = client.execute(new HttpGet("http://" + ipAddr + ":" + port + PATH + File.separator + fileName))) {
                logger.info("FILE DOWNLOAD: Request received from the node " + ipAddr + " : " + port + "Ready to download the file: " + fileName);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (FileOutputStream outStream = new FileOutputStream(file)) {
                        entity.writeTo(outStream);
                    }

                } else {
                    logger.error("FILE DOWNLOAD: Download Failed! Response body is empty in the file: " + fileName + " which downloaded from: " + ipAddr + " : " + port);
                    return false;
                }
                logger.info("FILE DOWNLOAD: File: " + fileName + " downloaded successfully!");

                logger.info("FILE DOWNLOAD: Start verifying the content...");
                String sha1Received = (response.getHeaders("Checksum-SHA1"))[0].toString().split(" ")[1];
                logger.info("FILE DOWNLOAD: Received hash value: " + sha1Received);

                String sha1Generated = DigestUtils.sha1Hex(Files.newInputStream(file.toPath())).toUpperCase();
                logger.info("FILE DOWNLOAD: Generated hash value: " + sha1Generated);
                String fileSize = (response.getHeaders("Content-Length"))[0].toString();

                if (sha1Generated.equals(sha1Received)) {
                    logger.info("FILE DOWNLOAD: Validation successful! Both hashed values match! Downloaded file size: " + fileSize);
                    return true;

                } else {
                    logger.error("FILE DOWNLOAD: Validation failed! hashed values do not match!");
                    return false;
                }

            } catch (Exception e) {
                logger.error("Exception occurred while downloading the file: " + fileName +
                        " from the Node: " + ipAddr + ":" + port, e);

            }
        }
        return false;
    }
}
