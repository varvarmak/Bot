package org.example.models;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void testEventCreationWithTimeString() {
        Event event = new Event("14:30", "Meeting", "Project discussion");
        assertEquals("14:30", event.getTime());
        assertEquals("Meeting", event.getTitle());
        assertEquals("Project discussion", event.getComm());
        assertFalse(event.isReminded());
        assertNotNull(event.getEventDateTime());
    }

    @Test
    public void testEventCreationWithLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 15, 30);
        Event event = new Event(dateTime, "Meeting", "Discussion");
        assertEquals("15:30", event.getTime());
        assertEquals("Meeting", event.getTitle());
        assertEquals(dateTime, event.getEventDateTime());
    }

    @Test
    public void testSettersAndGetters() {
        Event event = new Event("10:00", "Initial", "Description");
        event.setTime("11:00");
        event.setTitle("New Title");
        event.setComm("New Description");
        event.setReminded(true);

        assertEquals("11:00", event.getTime());
        assertEquals("New Title", event.getTitle());
        assertEquals("New Description", event.getComm());
        assertTrue(event.isReminded());
    }

    @Test
    public void testGetFormattedTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 9, 5);
        Event event = new Event(dateTime, "Test", "Test");
        assertEquals("09:05", event.getFormattedTime());
    }

    @Test
    public void testGetFormattedDate() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 15, 30);
        Event event = new Event(dateTime, "Test", "Test");
        assertEquals("14.12.2024", event.getFormattedDate());
    }

    @Test
    public void testMassageEvent() {
        Event event = new Event("14:30", "Meeting", "Discussion");
        assertEquals("14:30 - Meeting (Discussion)", event.massageEvent());
    }

    @Test
    public void testFormatEvent() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 14, 14, 30);
        Event event = new Event(dateTime, "Meeting", "Discussion");
        assertEquals("14:30 - Meeting (Discussion)", event.formatEvent());
    }
}