package com.navara;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        PowerStatusBot powerStatusBot = new PowerStatusBot();
        powerStatusBot.run();
//        try {
//            String botToken = "7112784009:AAGvz6nUMt0tbQw_sdjtR5XBO1q1c_9XX64";
//            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
//            botsApplication.registerBot(botToken, new MyAmazingBot());
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }
}
