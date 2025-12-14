package org.example.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class YearTest {

    @Test
    public void testYearCreation() {
        Year year = new Year(2024);
        assertEquals(2024, year.getYearNumber());
        assertEquals(0, year.getTotalEvents());
    }

    @Test
    public void testGetMonth() {
        Year year = new Year(2024);
        Month month = year.getMonth(6);
        assertNotNull(month);
        assertEquals(6, month.getMonthNumber());

        // Should cache the month
        Month sameMonth = year.getMonth(6);
        assertSame(month, sameMonth);
    }

    @Test
    public void testGetExistingMonth() {
        Year year = new Year(2024);
        assertNull(year.getExistingMonth(6)); // Not created yet

        year.getMonth(6); // Create month
        assertNotNull(year.getExistingMonth(6)); // Now exists
    }

    @Test
    public void testAddEvent() {
        Year year = new Year(2024);
        year.addEvent(6, 15, "14:30", "Meeting", "Discussion");

        Month month = year.getExistingMonth(6);
        assertNotNull(month);

        Day day = month.getExistingDay(15);
        assertNotNull(day);
        assertEquals(1, day.getEventsCount());
    }

    @Test
    public void testGetTotalEvents() {
        Year year = new Year(2024);
        year.addEvent(1, 10, "09:00", "Meeting 1", "Desc 1");
        year.addEvent(6, 15, "14:00", "Meeting 2", "Desc 2");
        year.addEvent(12, 31, "23:59", "Meeting 3", "Desc 3");

        assertEquals(3, year.getTotalEvents());
    }
}