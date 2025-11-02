package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DayTest {

    @Test
    public void testDayCreation() {
        Day day = new Day(15);
        assertEquals(15, day.getDayNumber());
        assertEquals(0, day.getEventsCount());
    }

    @Test
    public void testAddEvent() {
        Day day = new Day(1);
        day.addEvent("10:00", "Event 1", "Description 1");

        assertEquals(1, day.getEventsCount());

        Event event = day.getEvent("10:00");
        assertNotNull(event);
        assertEquals("10:00", event.getTime());
        assertEquals("Event 1", event.getTitle());
    }

    @Test
    public void testAddEventInvalidTime() {
        Day day = new Day(1);

        assertThrows(IllegalArgumentException.class, () -> {
            day.addEvent("25:00", "Invalid", "Test");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            day.addEvent("12:60", "Invalid", "Test");
        });
    }

    @Test
    public void testMessageDay() {
        Day day = new Day(5);
        day.addEvent("09:00", "Breakfast", "Morning meal");

        String expected = "День 5, дел: 1";
        assertEquals(expected, day.messageDay());
    }
}