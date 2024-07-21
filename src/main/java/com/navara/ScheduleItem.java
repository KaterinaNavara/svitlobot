package com.navara;

import java.time.LocalTime;

public class ScheduleItem {
   private LocalTime startTime;
   private LocalTime endTime;
    private Zone zone;

    public ScheduleItem(String startTime, String endTime, Zone zone) {
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
        this.zone = zone;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Zone getZone() {
        return zone;
    }
    @Override
    public String toString(){
        return String.format("{startTime - %s, endTime - %s, zone - %s }", getStartTime(), getEndTime(), getZone());
    }
}