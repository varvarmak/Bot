package org.example.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class MonthTest {

    @Test
    public void testMonthCreation() {
        Month month = new Month(6); // June
        assertEquals(6, month.getMonthNumber());
        assertEquals(30, month.getDaysInMonth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMonthCreationInvalid() {
        new Month(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMonthCreationInvalid2() {
        new Month(13);
    }

    @Test
    public void testGetDay() {
        Month month = new Month(6);
        Day day = month.getDay(15);
        assertNotNull(day);
        assertEquals(15, day.getDayNumber());

        // Should cache the day
        Day sameDay = month.getDay(15);
        assertSame(day, sameDay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDayInvalid() {
        Month month = new Month(6);
        month.getDay(32);
    }

    @Test
    public void testAddEvent() {
        Month month = new Month(6);
        month.addEvent(15, "14:30", "Meeting", "Discussion");

        Day day = month.getExistingDay(15);
        assertNotNull(day);
        assertEquals(1, day.getEventsCount());
    }

    @Test
    public void testGetExistingDay() {
        Month month = new Month(6);
        assertNull(month.getExistingDay(10)); // Not created yet

        month.getDay(10); // Create day
        assertNotNull(month.getExistingDay(10)); // Now exists
    }

    @Test
    public void testGetTotalEvents() {
        Month month = new Month(6);
        month.addEvent(1, "09:00", "Meeting 1", "Desc 1");
        month.addEvent(1, "14:00", "Meeting 2", "Desc 2");
        month.addEvent(15, "18:00", "Meeting 3", "Desc 3");

        assertEquals(3, month.getTotalEvents());
    }

    @Test
    public void testGetMonthNameStatic() {
        assertEquals("Январь", Month.getMonthName(1));
        assertEquals("Июнь", Month.getMonthName(6));
        assertEquals("Декабрь", Month.getMonthName(12));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMonthNameInvalid() {
        Month.getMonthName(0);
    }
}