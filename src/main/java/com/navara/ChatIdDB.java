package com.navara;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static com.navara.BotManager.getChatIdsFromUpdates;

public class ChatIdDB {
    private final static String chatIdsStoragePath = "/Users/knavara/IdeaProjects/svitlo_bot/src/main/resources/chatIds.txt";

    private static final String dbFile = chatIdsStoragePath;

    public ChatIdDB() {
        File file = new File(dbFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Set<Long> loadChatIds() {
        Set<Long> chatIds = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chatIds.add(Long.parseLong(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatIds;
    }

    public static Set<Long> updateChatIdsFile() {
        Set<Long> chatIds = loadChatIds();
        Set<Long> updates = getChatIdsFromUpdates();
        if(!chatIds.containsAll(updates)){
            updates.forEach(id ->{
                if(!chatIds.contains(id)){
                    saveChatId(id);
                }
            });
            chatIds.addAll(updates);
        }
        return chatIds;
    }

    public static void saveChatId(Long chatId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, true))) {
            writer.write(chatId.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeChatId(Long chatId) {
        Set<Long> chatIds = loadChatIds();
        chatIds.remove(chatId);
        saveAllChatIds(chatIds);
    }

    private void saveAllChatIds(Set<Long> chatIds) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile))) {
            for (Long id : chatIds) {
                writer.write(id.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
