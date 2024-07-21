package com.navara;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeHandler {
    public static DayOfWeek currentDayOfWeek;

    public static String getOutageDuration(LocalDateTime lastUpdateTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(lastUpdateTime, currentTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        if (hours != 0) {
            return String.format("%d год %d хв", hours, minutes);
        } else {
            return String.format("%d хв", minutes);
        }
    }

    public static String getCurrentTimeFormatted() {
        LocalTime currentTime = LocalTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = currentTime.format(formatter);
        return formattedTime;
    }

    public static DayOfWeek getCurrentDayOfWeek() {
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek;
    }

    public static void updateCurrentDayOfWeek(){
        currentDayOfWeek = getCurrentDayOfWeek();
    }

    public static boolean isNewDay(){
        return currentDayOfWeek!=getCurrentDayOfWeek();
    }

}
