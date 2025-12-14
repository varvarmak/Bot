package org.example.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User(1, "Test User");
        assertEquals(1, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals(0, user.getTotalEvents());
    }

    @Test
    public void testGetYear() {
        User user = new User(1, "Test");
        // Используем год >= 2025 (START_YEAR)
        Year year2025 = user.getYear(2025);
        Year year2026 = user.getYear(2026);

        assertNotNull(year2025);
        assertNotNull(year2026);
        assertEquals(2025, year2025.getYearNumber());
        assertEquals(2026, year2026.getYearNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetYearInvalid() {
        User user = new User(1, "Test");
        user.getYear(1900); // Ниже START_YEAR
    }

    @Test
    public void testGetExistingYear() {
        User user = new User(1, "Test");
        // Проверяем существующий год (2025+)
        assertNull(user.getExistingYear(2025)); // Не создавали

        user.getYear(2025); // Создаем год
        assertNotNull(user.getExistingYear(2025)); // Теперь существует
    }

    @Test
    public void testSetName() {
        User user = new User(1, "Old Name");
        user.setName("New Name");
        assertEquals("New Name", user.getName());
    }

    @Test
    public void testGetTotalEvents() {
        User user = new User(1, "Test");
        // Используем годы >= 2025
        user.getYear(2025).addEvent(6, 15, "14:30", "Meeting 1", "Desc 1");
        user.getYear(2025).addEvent(6, 15, "16:00", "Meeting 2", "Desc 2");
        user.getYear(2026).addEvent(1, 1, "00:00", "New Year", "Celebration");

        assertEquals(3, user.getTotalEvents());
    }

    @Test
    public void testEqualsAndHashCode() {
        User user1 = new User(1, "User 1");
        User user2 = new User(1, "User 2"); // Тот же ID, другое имя
        User user3 = new User(2, "User 1"); // Другой ID, то же имя

        assertEquals(user1, user2); // Одинаковые ID
        assertNotEquals(user1, user3); // Разные ID
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    public void testGetYearEdgeCase() {
        User user = new User(1, "Test");
        // Проверяем граничные значения
        Year year2025 = user.getYear(2025); // START_YEAR
        Year year2124 = user.getYear(2124); // START_YEAR + TOTAL_YEARS - 1

        assertNotNull(year2025);
        assertNotNull(year2124);

        // Проверяем исключение для года выше диапазона
        try {
            user.getYear(2125); // START_YEAR + TOTAL_YEARS
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Ожидаемое исключение
        }
    }
}