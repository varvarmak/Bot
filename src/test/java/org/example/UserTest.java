package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User(1, "John Doe");

        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals(0, user.getTotalEvents());
    }

    @Test
    public void testUserNameChange() {
        User user = new User(1, "Old Name");
        user.setName("New Name");

        assertEquals("New Name", user.getName());
    }

    @Test
    public void testYearOperations() {
        User user = new User(1, "Test User");

        Year year2025 = user.getYear(2025);
        assertNotNull(year2025);
        assertEquals(2025, year2025.getYearNumber());

        Year sameYear = user.getYear(2025);
        assertSame(year2025, sameYear);
    }

    @Test
    public void testInvalidYear() {
        User user = new User(1, "Test User");

        assertThrows(IllegalArgumentException.class, () -> user.getYear(2024));
        assertThrows(IllegalArgumentException.class, () -> user.getYear(2126));
    }

    @Test
    public void testUserEquality() {
        User user1 = new User(1, "User");
        User user2 = new User(1, "User");
        User user3 = new User(2, "Another User");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}