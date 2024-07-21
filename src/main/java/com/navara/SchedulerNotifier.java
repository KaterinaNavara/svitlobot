package com.navara;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.navara.Scheduler.getSchedule;

public class SchedulerNotifier {
    private static Map<ScheduleItem, Boolean> scheduleNotifier = new HashMap<>();
    private static Map<ScheduleItem, Boolean> scheduleNotifierForNextDay = new HashMap<>();

    public static void initialize() {
        scheduleNotifier.clear();
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        ArrayList<ScheduleItem> items = getSchedule().get(dayOfWeek);
        if (scheduleNotifierForNextDay.isEmpty()) {
            for (ScheduleItem item : items) {
                scheduleNotifier.put(item, false);
            }
        } else scheduleNotifier.putAll(scheduleNotifierForNextDay);
    }

    public static void initializeForNextDay() {
        scheduleNotifierForNextDay.clear();
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek().plus(1);
        ArrayList<ScheduleItem> items = getSchedule().get(dayOfWeek);
        for (ScheduleItem item : items) {
            scheduleNotifierForNextDay.put(item, false);
        }
    }

    public static Map<ScheduleItem, Boolean> getSchedulerNotifier() {
        return scheduleNotifier;
    }
    public static Map<ScheduleItem, Boolean> getScheduleNotifierForNextDay() {
        if(scheduleNotifierForNextDay.isEmpty()) initializeForNextDay();
        return scheduleNotifierForNextDay;
    }

    public static void changeSchedulerNotifier(ScheduleItem key, Boolean value) {
        scheduleNotifier.put(key, value);
    }

    public static void changeSchedulerNotifierForNextDay(ScheduleItem key, Boolean value) {
        if(scheduleNotifierForNextDay.isEmpty()) initializeForNextDay();
        scheduleNotifierForNextDay.put(key, value);
    }
}
