package org.uom.cse2023.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileManager {
    private static final String[] files = {
            "adventures-of-tintin",
            "jack-and-jill",
            "glee",
            "the-vampire-diarie",
            "king-arthur",
            "windows-xp",
            "harry-potter",
            "kung-fu-panda",
            "lady-gaga",
            "twilight",
            "windows-8",
            "mission-impossible",
            "turn-up-the-music",
            "super-mario",
            "american-pickers",
            "microsoft-office-2010",
            "happy-feet",
            "modern-family",
            "american-idol",
            "hacking-for-dummies"
    };
    private static FileManager fileManager;

    private final List<String> myFiles = new ArrayList<>();

    private FileManager() {
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            myFiles.add(files[r.nextInt(files.length)].toLowerCase());
        }
    }

    public static FileManager getIntance() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
    }

    public List<String> getMatchingFiles(String name) {
        String keyword = name.toLowerCase();
        List<String> searchResult = new ArrayList<>();
        for (String file : myFiles) {
            if (file.contains(keyword)) {
                searchResult.add(file);
            }
        }
        return searchResult;
    }

    public List<String> getMyFiles() {
        return myFiles;
    }

    public static String[] getFiles() {
        return files;
    }
}
