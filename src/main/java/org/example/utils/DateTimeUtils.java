package org.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {

    public static LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {
            String dateTimeStr = dateStr + " " + timeStr;
            return LocalDateTime.parse(dateTimeStr,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты или времени");
        }
    }

    public static boolean isValidTime(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    public static boolean isValidDate(int year, int month, int day) {
        try {
            LocalDateTime.of(year, month, day, 0, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}