package org.example.models;

import java.util.HashMap;
import java.util.Map;

public class Month {
    private int monthNumber;
    private int daysInMonth;
    private Map<Integer, Day> days;

    private static final int[] DAYS_IN_MONTH = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final String[] MONTH_NAMES = {"", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    public Month(int monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            throw new IllegalArgumentException("Неверный номер месяца: " + monthNumber);
        }

        this.monthNumber = monthNumber;
        this.daysInMonth = DAYS_IN_MONTH[monthNumber];
        this.days = new HashMap<>();
    }

    public int getMonthNumber() { return monthNumber; }

    public int getDaysInMonth() { return daysInMonth; }


    public Day getDay(int dayNumber) {
        if (dayNumber < 1 || dayNumber > daysInMonth) {
            throw new IllegalArgumentException("Неверный номер дня: " + dayNumber);
        }

        if (!days.containsKey(dayNumber)) {
            days.put(dayNumber, new Day(dayNumber));
        }
        return days.get(dayNumber);
    }

    public void addEvent(int dayNumber, String time, String title, String comm) {
        Day day = getDay(dayNumber);
        day.addEvent(time, title, comm);
    }

    public Day getExistingDay(int dayNumber) {
        return days.get(dayNumber);
    }

    public int getTotalEvents() {
        int total = 0;
        for (Day d : days.values()) {
            total += d.getEventsCount();
        }
        return total;
    }

    public static String getMonthName(int monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            throw new IllegalArgumentException("неверный номер месяца: " + monthNumber);
        }
        return MONTH_NAMES[monthNumber];
    }
}