package org.example.models;

public class Day {
    private int dayNumber;
    private Event[] events;

    public Day(int dayNumber) {
        this.dayNumber = dayNumber;
        this.events = new Event[24];
    }

    public void addEvent(String time, String title, String comm) {
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        if ((hour < 0 || hour > 23)||(minute < 0 || minute > 59)) {
            throw new IllegalArgumentException("неверное время: " + time);
        }

        events[hour] = new Event(time, title, comm);
    }

    public int getDayNumber() { return dayNumber; }

    public Event[] getEvents() {
        return events;
    }

    public Event getEvent(String time) {
        int hour = Integer.parseInt(time.split(":")[0]);
        return events[hour];
    }

    public int getEventsCount() {
        int count = 0;
        for (Event event : events) {
            if (event != null) {
                count++;
            }
        }
        return count;
    }

    public String messageDay() {
        return "День " + dayNumber + ", дел: " + getEventsCount();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(messageDay()).append("\n");

        for (Event e : events) {
            if (e != null) {
                sb.append(" ").append(e.massageEvent()).append("\n");
            }
        }
        return sb.toString();
    }
}