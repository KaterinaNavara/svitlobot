package com.navara;

import lombok.SneakyThrows;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.navara.Message.LIGHT_OFF_AFTER_15_MINUTES;
import static com.navara.Message.LIGHT_ON_AFTER_15_MINUTES;
import static com.navara.Messenger.sendMessage;
import static com.navara.Scheduler.*;
import static com.navara.SchedulerNotifier.*;
import static com.navara.TimeHandler.*;

public class PowerStatusBot extends Thread {

    private static final String DDNS_HOST = "BE061A8940D2A04241456C121AF624944.asuscomm.com";
    private static LastUpdateDB lastUpdateDB = new LastUpdateDB();

    private static final int DDNS_PORT = 8081;
    private LocalDateTime lastUpdateTime;
    LocalDate date = LocalDate.now();

    private PowerStatus currentStatus = null;
    private PowerStatus previousStatus = null;
    String clock = "\uD83D\uDD57";
    String greenCircle = "\uD83D\uDFE2";
    String redCircle = "\uD83D\uDD34";
    String calendar = "\uD83D\uDCC5";


    private static Map<DayOfWeek, ArrayList<ScheduleItem>> schedule = new HashMap<>();

    static {
        schedule = getSchedule();
    }

    @SneakyThrows
    public void run() {
        BotManager.initializeIdList();
        initializeLastUpdateTime();
        initializeStatus();
        while (true) {
            if (isNewDay()) {
                updateCurrentDayOfWeek();
                SchedulerNotifier.initialize();
            }
            updatePowerStatus();
            if (isStatusChanged()) {
                sendMessage(generateMessage());
                updateLastUpdateTime();
            }
            String message = generateMessageLessThan15Minutes();
            if (message != null) sendMessage(message);
            Thread.sleep(5000);
        }
    }

    private void initializeLastUpdateTime() {
        System.out.println("Initialize last time update");
        lastUpdateTime = lastUpdateDB.readLastUpdateTime();
        System.out.printf("lastUpdateTime initialized with value: " + lastUpdateTime);
    }

    private void updatePowerStatus() {
        System.out.println("Updating power status...");
        previousStatus = currentStatus;
        boolean isOn = RouterPinger.pingRouter(DDNS_HOST, DDNS_PORT);
        if (isOn) {
            currentStatus = PowerStatus.ON;
        } else {
            currentStatus = PowerStatus.OFF;
        }
        System.out.printf("Current status: %s%n", currentStatus.getStatus());
    }

    public void initializeStatus() {
        System.out.println("Initializing power bot...");
        boolean result = RouterPinger.pingRouter(DDNS_HOST, DDNS_PORT);
        if (result) {
            currentStatus = PowerStatus.ON;
            previousStatus = PowerStatus.ON;
        } else {
            currentStatus = PowerStatus.OFF;
            previousStatus = PowerStatus.OFF;
        }
        updateLastUpdateTime();
        updateCurrentDayOfWeek();
        SchedulerNotifier.initialize();
        System.out.printf("Bot initialized. Current power status: %s%n", currentStatus.getStatus());
    }

    private boolean isStatusChanged() {
        return currentStatus != previousStatus;
    }

    private void updateLastUpdateTime() {
        lastUpdateTime = java.time.LocalDateTime.now();
        lastUpdateDB.writeLastUpdateTime(lastUpdateTime);
    }

    private String generateMessage() {
        if (currentStatus == PowerStatus.ON) {
            return String.format("%s %s Cвітло з'явилося.\n" +
                    clock + " Його не було %s \n" +
                    calendar + "наступне виключення має початись о %s", greenCircle, getCurrentTimeFormatted(), getOutageDuration(lastUpdateTime), getNextBlackoutStart());
        } else {
            LocalTime[] turnOnOptions = getNextGreyAndWhiteStart();
            return
                    String.format("%s %s Cвітло зникло.\n" +
                            clock + " Воно було %s \n" +
                            calendar + "світло мають включити о %s (сіра зона) aбо %s (гарантоване світло)", redCircle, getCurrentTimeFormatted(), getOutageDuration(lastUpdateTime), turnOnOptions[0], turnOnOptions[1]);
        }
    }

    private String generateMessageLessThan15Minutes() {
        Map<ScheduleItem, Boolean> scheduleItemBooleanMap = getSchedulerNotifier();
        if (isLessThan15MinutesToNextWhiteZone() && currentStatus == PowerStatus.OFF) {
            ScheduleItem nextWhiteZone = getNextWhiteZone();
            if (scheduleItemBooleanMap.containsKey(nextWhiteZone) && !scheduleItemBooleanMap.get(nextWhiteZone)) {
                changeSchedulerNotifier(nextWhiteZone, true);
                System.out.println("Генерую месседж для скедулера сьогодні: " + LIGHT_ON_AFTER_15_MINUTES);
                System.out.println("наступна світла зона: " + nextWhiteZone);
                return calendar + LIGHT_ON_AFTER_15_MINUTES;
            } else {
                Map<ScheduleItem, Boolean> scheduleItemBooleanMapForNextDay = getScheduleNotifierForNextDay();
                if (scheduleItemBooleanMapForNextDay.containsKey(nextWhiteZone) && !scheduleItemBooleanMapForNextDay.get(nextWhiteZone)) {
                    changeSchedulerNotifierForNextDay(nextWhiteZone, true);
                    System.out.println("Генерую месседж для скедулера завтра: " + LIGHT_ON_AFTER_15_MINUTES);
                    System.out.println("наступна світла зона: " + nextWhiteZone);
                    return calendar + LIGHT_ON_AFTER_15_MINUTES;
                }
            }
        } else if (isLessThan15MinutesToNextBlackZone() && currentStatus == PowerStatus.ON) {
            ScheduleItem nextBlackZone = getNextBlackZone();
            if (scheduleItemBooleanMap.containsKey(nextBlackZone) && !scheduleItemBooleanMap.get(nextBlackZone)) {
                changeSchedulerNotifier(nextBlackZone, true);
                System.out.println("Генерую месседж для скедулера сьогодні: " + LIGHT_OFF_AFTER_15_MINUTES);
                System.out.println("наступна темна зона: " + nextBlackZone.toString());
                return calendar + LIGHT_OFF_AFTER_15_MINUTES;
            } else {
                Map<ScheduleItem, Boolean> scheduleItemBooleanMapForNextDay = getScheduleNotifierForNextDay();
                if (scheduleItemBooleanMapForNextDay.containsKey(nextBlackZone) && !scheduleItemBooleanMapForNextDay.get(nextBlackZone)) {
                    changeSchedulerNotifierForNextDay(nextBlackZone, true);
                    System.out.println("Генерую месседж для скедулера завтра: " + LIGHT_OFF_AFTER_15_MINUTES);
                    System.out.println("наступна світла зона: " + nextBlackZone.toString());
                    return calendar + LIGHT_OFF_AFTER_15_MINUTES;
                }
            }
        }
        return null;
    }


    //    private void sendMessageStatusChanged() {
//        if (isStatusChanged()) {
//            sendMessage(generateMessage());
//        }
//    }
//
//    private void checkPowerStatus(Long chatId) {
//        // Получение текущего дня недели и времени
//        LocalDateTime now = LocalDateTime.now();
//        DayOfWeek currentDayOfWeek = now.getDayOfWeek();
//        LocalTime currentTime = now.toLocalTime();
//
//        // Поиск ближайшего планируемого отключения
//        ScheduleItem nextScheduledItem = getNextScheduledItem(currentDayOfWeek, currentTime);
//
//        // Формирование сообщения о статусе электропитания
//        String messageText = generateStatusMessage(nextScheduledItem);
//
//        // Отправка сообщения в Telegram
//        SendMessage message = new SendMessage(YOUR_CHAT_ID, messageText);
//        try {
//            telegramBotsApi.execute(message); // Отправляем сообщение в Telegram
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//
    private ScheduleItem getNextScheduledItem() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDayOfWeek = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        ArrayList<ScheduleItem> daySchedule = schedule.get(currentDayOfWeek);

        if (daySchedule != null) {
            for (int i = 0; i < daySchedule.size(); i++) {
                ScheduleItem item = daySchedule.get(i);
                LocalTime startTime = item.getStartTime();
                LocalTime endTime = item.getEndTime();

                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    if ((i + 1) == daySchedule.size()) {
                        DayOfWeek nextDayOfWeek = currentDayOfWeek.plus(1);
                        ArrayList<ScheduleItem> nextDaySchedule = schedule.get(nextDayOfWeek);
                        return nextDaySchedule.get(0);
                    } else return daySchedule.get(i + 1);
                }
            }
        }

        return null;
    }

//    private String generateStatusMessage(ScheduleItem nextScheduledItem) {
//        if (nextScheduledItem != null) {
//            String startTime = nextScheduledItem.getStartTime();
//            String endTime = nextScheduledItem.getEndTime();
//            String zone = nextScheduledItem.getZone();
//
//            // Формирование сообщения в зависимости от текущей и следующей зоны
//            switch (zone) {
//                case "gray":
//                    return String.format("Свет выключен. Следующий период включения: %s - %s",
//                            startTime, endTime);
//                case "black":
//                    return String.format("Свет выключен. Следующий период включения: %s - %s или %s - %s",
//                            startTime, endTime, "время начала следующего серого интервала", "время начала следующего белого интервала");
//                case "white":
//                default:
//                    return String.format("Свет включен. Следующий плановый отключение: %s - %s",
//                            startTime, endTime);
//            }
//        } else {
//            return "Свет включен. Нет плановых отключений на сегодня.";
//        }
//    }
//
//    public static void main(String[] args) {
//        com.navara.PowerStatusBot bot = new com.navara.PowerStatusBot();
//
//        try {
//            bot.execute(SendMessage.builder().chatId(YOUR_CHAT_ID).text("Bot started").build()); // Отправка сообщения о запуске бота
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//
//        // Запуск бота
//        bot.run();
//    }

}