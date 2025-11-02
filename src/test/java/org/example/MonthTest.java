package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonthTest {

    @Test
    public void testMonthCreation() {
        Month month = new Month(5);
        assertEquals(5, month.getMonthNumber());
        assertEquals("Май", month.getMonthName());
        assertEquals(31, month.getDaysInMonth());
    }

    @Test
    public void testInvalidMonth() {
        assertThrows(IllegalArgumentException.class, () -> new Month(0));
        assertThrows(IllegalArgumentException.class, () -> new Month(13));
    }

    @Test
    public void testAddEvent() {
        Month month = new Month(6);
        month.addEvent(15, "14:00", "Meeting", "Team meeting");

        Day day = month.getExistingDay(15);
        assertNotNull(day);
        assertEquals(1, day.getEventsCount());
    }

    @Test
    public void testGetMonthNameStatic() {
        assertEquals("Январь", Month.getMonthName(1));
        assertEquals("Декабрь", Month.getMonthName(12));

        assertThrows(IllegalArgumentException.class, () -> Month.getMonthName(0));
    }
}