package org.example.services;

public class UserData {
    private int year;
    private String month;
    private int day;
    private String reminderTime; // Changed to String
    private String reminderName;
    private String description;

    // Add getters and setters for all fields
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public String getReminderName() { return reminderName; }
    public void setReminderName(String reminderName) { this.reminderName = reminderName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}