package org.example.models;

import java.time.LocalDateTime;

public class EventData {
    public int year;
    public int month;
    public int day;
    public String time;
    public String title;
    public LocalDateTime eventDateTime;

    public EventData() {
        this.eventDateTime = LocalDateTime.now();
    }
}