package org.example.utils;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class DateTimeUtilsTest {

    @Test
    public void testParseDateTimeValid() {
        String dateStr = "14.12.2024";
        String timeStr = "15:30";

        LocalDateTime result = DateTimeUtils.parseDateTime(dateStr, timeStr);
        assertNotNull(result);
        assertEquals(2024, result.getYear());
        assertEquals(12, result.getMonthValue());
        assertEquals(14, result.getDayOfMonth());
        assertEquals(15, result.getHour());
        assertEquals(30, result.getMinute());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseDateTimeInvalid() {
        String invalidDate = "32.13.2024";
        String validTime = "15:30";
        DateTimeUtils.parseDateTime(invalidDate, validTime);
    }

    @Test
    public void testIsValidTime() {
        // Valid cases
        assertTrue(DateTimeUtils.isValidTime("00:00"));
        assertTrue(DateTimeUtils.isValidTime("12:30"));
        assertTrue(DateTimeUtils.isValidTime("23:59"));
        assertTrue(DateTimeUtils.isValidTime("09:05"));

        // Invalid cases
        assertFalse(DateTimeUtils.isValidTime("24:00"));
        assertFalse(DateTimeUtils.isValidTime("12:60"));
        assertFalse(DateTimeUtils.isValidTime("12:"));
        assertFalse(DateTimeUtils.isValidTime(":30"));
        assertFalse(DateTimeUtils.isValidTime(""));
        assertFalse(DateTimeUtils.isValidTime("abc"));
        assertFalse(DateTimeUtils.isValidTime("12.30"));
    }

    @Test
    public void testIsValidDate() {
        // Valid dates
        assertTrue(DateTimeUtils.isValidDate(2024, 1, 1));
        assertTrue(DateTimeUtils.isValidDate(2024, 12, 31));
        assertTrue(DateTimeUtils.isValidDate(2024, 2, 29)); // Leap year

        // Invalid dates
        assertFalse(DateTimeUtils.isValidDate(2024, 0, 1));
        assertFalse(DateTimeUtils.isValidDate(2024, 13, 1));
        assertFalse(DateTimeUtils.isValidDate(2024, 1, 0));
        assertFalse(DateTimeUtils.isValidDate(2024, 1, 32));
        assertFalse(DateTimeUtils.isValidDate(2024, 2, 30));
        assertFalse(DateTimeUtils.isValidDate(2023, 2, 29)); // Not leap year
    }

    @Test
    public void testFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 15, 30);
        String formatted = DateTimeUtils.formatDateTime(dateTime);
        assertEquals("14.12.2024 15:30", formatted);
    }

    @Test
    public void testFormatDate() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 15, 30);
        String formatted = DateTimeUtils.formatDate(dateTime);
        assertEquals("14.12.2024", formatted);
    }

    @Test
    public void testFormatTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 9, 5);
        String formatted = DateTimeUtils.formatTime(dateTime);
        assertEquals("09:05", formatted);
    }
}