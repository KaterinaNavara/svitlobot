package com.navara;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static com.navara.ChatIdDB.updateChatIdsFile;

public class BotManager {
    private final static Set<Long> chatIds = new HashSet<>();
    private static final String MY_TOKEN = "7112784009:AAGvz6nUMt0tbQw_sdjtR5XBO1q1c_9XX64";

    public static void initializeIdList() {
        ChatIdDB chatIdDB = new ChatIdDB();
        chatIds.addAll(updateChatIdsFile());
        System.out.println("InitializeIdList with values " + chatIds);
    }

    public static Set<Long> updateAndReturnChatIds() {
        chatIds.addAll(updateChatIdsFile());
        return chatIds;
    }

    @SneakyThrows
    public static Set<Long> getChatIdsFromUpdates() {
        Set<Long> chatIds = new HashSet<>();
        try {
            URL url = new URL("https://api.telegram.org/bot" + MY_TOKEN + "/getUpdates");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Парсим JSON ответ
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("result");

            // Извлекаем chat_id из каждого объекта Update
            for (JsonElement result : results) {
                JsonObject update = result.getAsJsonObject();
                JsonObject message = update.getAsJsonObject("message");
                long chatId = message.getAsJsonObject("chat").get("id").getAsLong();
                System.out.println("Add user with chatId " + chatId);
                chatIds.add(chatId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatIds;
    }

    public static String getMyToken() {
        return MY_TOKEN;
    }
}