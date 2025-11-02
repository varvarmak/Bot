package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void testEventCreation() {
        Event event = new Event("14:30", "Meeting", "meeting");

        assertEquals("14:30", event.getTime());
        assertEquals("Meeting", event.getTitle());
        assertEquals("meeting", event.getComm());
        assertFalse(event.isReminded());
    }

    @Test
    public void testEventSetters() {
        Event event = new Event("10:00", "Test", "Test");

        event.setTime("15:00");
        event.setTitle("Title");
        event.setComm("Description");
        event.setReminded(true);

        assertEquals("15:00", event.getTime());
        assertEquals("Title", event.getTitle());
        assertEquals("Description", event.getComm());
        assertTrue(event.isReminded());
    }

    @Test
    public void testMassageEvent() {
        Event event = new Event("09:00", "Standup", "Daily meeting");
        String expected = "09:00 - Standup (Daily meeting)";

        assertEquals(expected, event.massageEvent());
    }
}