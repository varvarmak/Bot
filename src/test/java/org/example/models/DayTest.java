package org.example.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class DayTest {

    @Test
    public void testDayCreation() {
        Day day = new Day(15);
        assertEquals(15, day.getDayNumber());
        assertEquals(0, day.getEventsCount());
        assertNotNull(day.getEvents());
        assertEquals(24, day.getEvents().length);
    }

    @Test
    public void testAddEvent() {
        Day day = new Day(10);
        day.addEvent("14:30", "Meeting", "Project discussion");

        assertEquals(1, day.getEventsCount());
        Event event = day.getEvent("14:30");
        assertNotNull(event);
        assertEquals("14:30", event.getTime());
        assertEquals("Meeting", event.getTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEventInvalidTime() {
        Day day = new Day(10);
        day.addEvent("25:00", "Invalid", "Test");
    }

    @Test
    public void testAddMultipleEvents() {
        Day day = new Day(20);
        day.addEvent("09:00", "Morning", "Standup");
        day.addEvent("14:00", "Afternoon", "Planning");
        day.addEvent("18:00", "Evening", "Review");

        assertEquals(3, day.getEventsCount());
        // Используем правильные названия
        assertEquals("Morning", day.getEvent("09:00").getTitle());
        assertEquals("Afternoon", day.getEvent("14:00").getTitle());
        assertEquals("Evening", day.getEvent("18:00").getTitle()); // ИСПРАВЛЕНО: должно быть "Evening", а не "Review"
    }

    @Test
    public void testGetEventNotFound() {
        Day day = new Day(10);
        day.addEvent("10:00", "Meeting", "Test");
        assertNull(day.getEvent("11:00"));
    }

    @Test
    public void testMessageDay() {
        Day day = new Day(15);
        day.addEvent("10:00", "Meeting", "Test");
        day.addEvent("14:00", "Lunch", "Break");

        String message = day.messageDay();
        assertTrue(message.contains("День 15"));
        assertTrue(message.contains("дел: 2"));
    }

    @Test
    public void testToString() {
        Day day = new Day(15);
        day.addEvent("10:00", "Meeting", "Discussion");

        String result = day.toString();
        assertTrue(result.contains("День 15"));
        assertTrue(result.contains("Meeting"));
        assertTrue(result.contains("Discussion"));
    }

    @Test
    public void testGetEventsArray() {
        Day day = new Day(15);
        day.addEvent("10:00", "Meeting", "Test");

        Event[] events = day.getEvents();
        assertNotNull(events);
        assertEquals(24, events.length);
        assertNotNull(events[10]); // Индекс соответствует часу
        assertNull(events[11]); // Этот час не заполнен
    }
}