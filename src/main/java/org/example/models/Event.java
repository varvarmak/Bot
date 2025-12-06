package org.example.models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private String time;
    private String title;
    private String comm;
    private boolean reminded = false;
    private LocalDateTime eventDateTime;

    public Event(String time, String title, String comm) {
        this.time = time;
        this.title = title;
        this.comm = comm;
        this.reminded = false;
        this.eventDateTime = LocalDateTime.now();
    }

    public Event(LocalDateTime eventDateTime, String title, String comm) {
        this.eventDateTime = eventDateTime;
        this.time = eventDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.title = title;
        this.comm = comm;
        this.reminded = false;
    }

    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getComm() { return comm; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public boolean isReminded() { return reminded; }

    public void setTime(String time) { this.time = time; }
    public void setTitle(String title) { this.title = title; }
    public void setComm(String comm) { this.comm = comm; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }
    public void setReminded(boolean reminded) { this.reminded = reminded; }

    public String getFormattedTime() {
        return eventDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getFormattedDate() {
        return eventDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String massageEvent() {
        return time + " - " + title + " (" + comm + ")";
    }

    public String formatEvent() {
        return getFormattedTime() + " - " + title + " (" + comm + ")";
    }
}