package com.navara;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static com.navara.BotManager.getChatIdsFromUpdates;

public class LastUpdateDB {
    private final static String lastUpdateStoragePath = "/Users/knavara/IdeaProjects/svitlo_bot/src/main/resources/lastUpdate.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Создание файла, если он не существует
    public LastUpdateDB() {
        try {
            File file = new File(lastUpdateStoragePath);
            if (!file.exists()) {
                file.createNewFile();
                writeLastUpdateTime(LocalDateTime.now());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LocalDateTime readLastUpdateTime() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(lastUpdateStoragePath)));
            if (content.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(content, FORMATTER);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeLastUpdateTime(LocalDateTime lastUpdateTime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(lastUpdateStoragePath))) {
            writer.write(lastUpdateTime.format(FORMATTER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
