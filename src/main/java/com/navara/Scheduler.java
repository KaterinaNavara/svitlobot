package com.navara;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.navara.Zone.*;


public class Scheduler {
    private static final Map<DayOfWeek, ArrayList<ScheduleItem>> schedule = new HashMap<>();

    public static Map<DayOfWeek, ArrayList<ScheduleItem>> getSchedule() {
        schedule.put(DayOfWeek.MONDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "04:00", BLACK),
                new ScheduleItem("04:00", "07:00", GREY),
                new ScheduleItem("07:00", "09:00", WHITE),
                new ScheduleItem("09:00", "13:00", BLACK),
                new ScheduleItem("13:00", "16:00", GREY),
                new ScheduleItem("16:00", "18:00", WHITE),
                new ScheduleItem("18:00", "22:00", BLACK),
                new ScheduleItem("22:00", "23:59", GREY))));
        schedule.put(DayOfWeek.TUESDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "01:00", GREY),
                new ScheduleItem("01:00", "03:00", WHITE),
                new ScheduleItem("03:00", "07:00", BLACK),
                new ScheduleItem("07:00", "10:00", GREY),
                new ScheduleItem("10:00", "12:00", WHITE),
                new ScheduleItem("12:00", "16:00", BLACK),
                new ScheduleItem("16:00", "19:00", GREY),
                new ScheduleItem("19:00", "21:00", WHITE),
                new ScheduleItem("21:00", "23:59", BLACK))));

        schedule.put(DayOfWeek.WEDNESDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "01:00", BLACK),
                new ScheduleItem("01:00", "04:00", GREY),
                new ScheduleItem("04:00", "06:00", WHITE),
                new ScheduleItem("06:00", "10:00", BLACK),
                new ScheduleItem("10:00", "13:00", GREY),
                new ScheduleItem("13:00", "15:00", WHITE),
                new ScheduleItem("15:00", "19:00", BLACK),
                new ScheduleItem("19:00", "22:00", GREY),
                new ScheduleItem("22:00", "23:59", WHITE))));

        schedule.put(DayOfWeek.THURSDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "04:00", BLACK),
                new ScheduleItem("04:00", "07:00", GREY),
                new ScheduleItem("07:00", "09:00", WHITE),
                new ScheduleItem("09:00", "13:00", BLACK),
                new ScheduleItem("13:00", "16:00", GREY),
                new ScheduleItem("16:00", "18:00", WHITE),
                new ScheduleItem("18:00", "22:00", BLACK),
                new ScheduleItem("22:00", "23:59", GREY))));

        schedule.put(DayOfWeek.FRIDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "01:00", GREY),
                new ScheduleItem("01:00", "03:00", WHITE),
                new ScheduleItem("03:00", "07:00", BLACK),
                new ScheduleItem("07:00", "10:00", GREY),
                new ScheduleItem("10:00", "12:00", WHITE),
                new ScheduleItem("12:00", "16:00", BLACK),
                new ScheduleItem("16:00", "19:00", GREY),
                new ScheduleItem("19:00", "21:00", WHITE),
                new ScheduleItem("21:00", "23:59", BLACK))));

        schedule.put(DayOfWeek.SATURDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "01:00", BLACK),
                new ScheduleItem("01:00", "04:00", GREY),
                new ScheduleItem("04:00", "06:00", WHITE),
                new ScheduleItem("06:00", "10:00", BLACK),
                new ScheduleItem("10:00", "13:00", GREY),
                new ScheduleItem("13:00", "15:00", WHITE),
                new ScheduleItem("15:00", "19:00", BLACK),
                new ScheduleItem("19:00", "22:00", GREY),
                new ScheduleItem("22:00", "23:59", WHITE))));

        schedule.put(DayOfWeek.SUNDAY, new ArrayList<>(Arrays.asList(
                new ScheduleItem("00:00", "04:00", BLACK),
                new ScheduleItem("04:00", "07:00", GREY),
                new ScheduleItem("07:00", "09:00", WHITE),
                new ScheduleItem("09:00", "13:00", BLACK),
                new ScheduleItem("13:00", "16:00", GREY),
                new ScheduleItem("16:00", "18:00", WHITE),
                new ScheduleItem("18:00", "22:00", BLACK),
                new ScheduleItem("22:00", "23:59", GREY))));
        return schedule;
    }

    public static LocalTime getNextBlackoutStart() {
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        ArrayList<ScheduleItem> items = schedule.get(dayOfWeek);

        LocalTime now = LocalTime.now();
        for (ScheduleItem item : items) {
            if (item.getZone() == Zone.BLACK && item.getStartTime().isAfter(now)) {
                return item.getStartTime();
            }
        }

        // If no BLACK zone found today, check the next day
        dayOfWeek = dayOfWeek.plus(1);
        items = schedule.get(dayOfWeek);
        for (ScheduleItem item : items) {
            if (item.getZone() == Zone.BLACK) {
                return item.getStartTime();
            }
        }

        return null; // No BLACK zone found
    }

    public static LocalTime getNextWhiteZoneStart() {
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        ArrayList<ScheduleItem> items = schedule.get(dayOfWeek);

        LocalTime now = LocalTime.now();
        for (ScheduleItem item : items) {
            if (item.getZone() == WHITE && item.getStartTime().isAfter(now)) {
                return item.getStartTime();
            }
        }

        dayOfWeek = dayOfWeek.plus(1);
        items = schedule.get(dayOfWeek);
        for (ScheduleItem item : items) {
            if (item.getZone() == WHITE) {
                return item.getStartTime();
            }
        }

        return null; // No BLACK zone found
    }

    public static LocalTime[] getNextGreyAndWhiteStart() {
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        ArrayList<ScheduleItem> items = schedule.get(dayOfWeek);

        LocalTime now = LocalTime.now();
        LocalTime nextGreyStart = null;
        LocalTime nextWhiteStart = null;

        for (ScheduleItem item : items) {
            if (item.getStartTime().isAfter(now)) {
                if (item.getZone() == Zone.GREY && nextGreyStart == null) {
                    nextGreyStart = item.getStartTime();
                } else if (item.getZone() == Zone.WHITE && nextWhiteStart == null) {
                    nextWhiteStart = item.getStartTime();
                }
            }
        }

        // If no GREY or WHITE zone found today, check the next day
        if (nextGreyStart == null || nextWhiteStart == null) {
            dayOfWeek = dayOfWeek.plus(1);
            items = schedule.get(dayOfWeek);
            for (ScheduleItem item : items) {
                if (item.getZone() == Zone.GREY && nextGreyStart == null) {
                    nextGreyStart = item.getStartTime();
                } else if (item.getZone() == Zone.WHITE && nextWhiteStart == null) {
                    nextWhiteStart = item.getStartTime();
                }
            }
        }

        return new LocalTime[]{nextGreyStart, nextWhiteStart};
    }


    public static Zone getCurrentZone() {
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        ArrayList<ScheduleItem> items = schedule.get(dayOfWeek);

        LocalTime now = LocalTime.now();
        for (ScheduleItem item : items) {
            if (!now.isBefore(item.getStartTime()) && !now.isAfter(item.getEndTime())) {
                return item.getZone();
            }
        }

        return null; // No current zone found
    }

    public static boolean isLessThan15MinutesToNextWhiteZone() {
        LocalTime nextWhiteStart = getNextWhiteZoneStart();
        ScheduleItem nextWhiteZone = getNextWhiteZone();
        LocalDate date = LocalDate.now();
        LocalTime now = LocalTime.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (nextWhiteStart != null) {
            if (schedule.get(dayOfWeek).contains(nextWhiteZone) && now.isBefore(nextWhiteStart)) {
                return now.plusMinutes(15).isAfter(nextWhiteStart);
            }
        } else if (schedule.get(dayOfWeek.plus(1)).contains(nextWhiteZone) && now.isAfter(nextWhiteStart)) {
            if (nextWhiteStart != null) {
                return now.plusMinutes(15).isAfter(nextWhiteStart.plusHours(24));
            }

        }
        return false;
    }

    public static boolean isLessThan15MinutesToNextBlackZone() {
        LocalTime nextBlackStart = getNextBlackoutStart();
        ScheduleItem nextBlackZone = getNextBlackZone();
        LocalDate date = LocalDate.now();
        LocalTime now = LocalTime.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (nextBlackStart != null) {
            if (schedule.get(dayOfWeek).contains(nextBlackZone) && now.isBefore(nextBlackStart)) {
                return now.plusMinutes(15).isAfter(nextBlackStart);
            } else if (schedule.get(dayOfWeek.plus(1)).contains(nextBlackZone) && now.isAfter(nextBlackStart)) {
                if (nextBlackStart != null) {
                    return now.plusMinutes(15).isAfter(nextBlackStart.plusHours(24));
                }
            }
        }
        return false;
    }

    public static ScheduleItem getNextWhiteZone() {
        LocalDate today = LocalDate.now();
        return getNextZone(today, Zone.WHITE);
    }

    public static ScheduleItem getNextBlackZone() {
        LocalDate today = LocalDate.now();
        return getNextZone(today, Zone.BLACK);
    }

    private static ScheduleItem getNextZone(LocalDate date, Zone zone) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        ArrayList<ScheduleItem> items = schedule.get(dayOfWeek);

        LocalTime now = LocalTime.now();
        ScheduleItem nextZoneItem = null;

        for (ScheduleItem item : items) {
            if (item.getZone() == zone && item.getStartTime().isAfter(now)) {
                nextZoneItem = item;
                break;
            }
        }

        // If no zone found today, check the next day
        if (nextZoneItem == null) {
            dayOfWeek = dayOfWeek.plus(1);
            items = schedule.get(dayOfWeek);
            for (ScheduleItem item : items) {
                if (item.getZone() == zone) {
                    nextZoneItem = item;
                    break;
                }
            }
        }

        return nextZoneItem;
    }
}
