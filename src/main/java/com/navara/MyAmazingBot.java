package com.navara;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
    private static final String MY_TOKEN = "7112784009:AAGvz6nUMt0tbQw_sdjtR5XBO1q1c_9XX64";
    private static final String CHAT_ID = "497341429";

    @Override
    public void consume(Update update) {
        TelegramClient telegramClient = new OkHttpTelegramClient(MY_TOKEN);
        SendMessage sendMessage = new SendMessage(CHAT_ID, "Hello world");
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
