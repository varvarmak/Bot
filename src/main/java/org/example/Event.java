package org.example;

public class Event {
    private String time;
    private String title;
    private String comm;

    public Event(String time, String title, String comm) {
        this.time = time;
        this.title = title;
        this.comm = comm;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getComm() {
        return comm;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }

    public String massageEvent() {
        return time + " - " + title + " (" + comm + ")";
    }
}
