package com.navara;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashSet;
import java.util.Set;

import static com.navara.BotManager.getMyToken;
import static com.navara.BotManager.updateAndReturnChatIds;

public class Messenger {
    private static final String CHAT_ID = "497341429";
    private static final String CHAT_ID2 = "584008426";
    private Set<Long> chatIds = new HashSet<>();

    public static void sendMessage(String message) {
        Set<Long> chatIds = updateAndReturnChatIds();
        TelegramClient telegramClient = new OkHttpTelegramClient(getMyToken());
        chatIds.forEach(chatId -> {
            SendMessage sendMessage = new SendMessage(chatId.toString(), message);
            System.out.println("Send message to user with chat id " + chatId + " message: " + message);

            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }
}
