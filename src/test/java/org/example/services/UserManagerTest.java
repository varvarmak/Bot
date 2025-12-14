package org.example.services;

import org.example.models.User;
import org.junit.Before;
import org.junit.Test;
import java.util.Collection;
import static org.junit.Assert.*;

public class UserManagerTest {

    private UserManager userManager;

    @Before
    public void setUp() {
        userManager = new UserManager();
    }

    @Test
    public void testCreateUser() {
        Long chatId = 12345L;
        String userName = "Test User";

        User user = userManager.createUser(chatId, userName);
        assertNotNull(user);
        assertEquals(userName, user.getName());
        assertTrue(user.getId() > 0);

        // Проверяем, что пользователь добавлен в мапы
        User byChatId = userManager.getUserByChatId(chatId);
        assertSame(user, byChatId);

        User byName = userManager.getUserByName(userName.toLowerCase());
        assertSame(user, byName);
    }

    @Test
    public void testGetUserByChatId() {
        Long chatId = 12345L;
        User expectedUser = userManager.createUser(chatId, "User");

        User result = userManager.getUserByChatId(chatId);
        assertSame(expectedUser, result);
    }

    @Test
    public void testGetUserByChatIdNotFound() {
        User result = userManager.getUserByChatId(99999L);
        assertNull(result);
    }

    @Test
    public void testGetUserByName() {
        Long chatId = 12345L;
        String userName = "Ivan Ivanov";
        User expectedUser = userManager.createUser(chatId, userName);

        User result = userManager.getUserByName(userName.toLowerCase());
        assertSame(expectedUser, result);
    }

    @Test
    public void testGetUserByNameCaseInsensitive() {
        Long chatId = 12345L;
        userManager.createUser(chatId, "Ivan");

        assertNotNull(userManager.getUserByName("ivan")); // lowercase
        assertNotNull(userManager.getUserByName("IVAN")); // uppercase
    }

    @Test
    public void testSwitchUser() {
        Long chatId = 12345L;
        User user1 = userManager.createUser(chatId, "User 1");
        User user2 = new User(999, "User 2");

        userManager.switchUser(chatId, user2);
        assertSame(user2, userManager.getUserByChatId(chatId));
    }

    @Test
    public void testUpdateUserName() {
        Long chatId = 12345L;
        User user = userManager.createUser(chatId, "Old Name");

        userManager.updateUserName(user, "Old Name", "New Name");
        assertNull(userManager.getUserByName("old name"));
        assertSame(user, userManager.getUserByName("new name"));
        // ПРОВЕРЯЕМ, что имя изменилось в самом объекте User
        assertEquals("New Name", user.getName()); // Убедитесь что user.setName() вызывается
    }

    @Test
    public void testGetAvailableUsersList() {
        userManager.createUser(11111L, "User 1");
        userManager.createUser(22222L, "User 2");

        String list = userManager.getAvailableUsersList();
        assertTrue(list.contains("User 1"));
        assertTrue(list.contains("User 2"));
    }

    @Test
    public void testUserExists() {
        Long chatId = 12345L;
        userManager.createUser(chatId, "User");

        assertTrue(userManager.userExists(chatId));
        assertFalse(userManager.userExists(99999L));
    }

    @Test
    public void testGetAllUsers() {
        userManager.createUser(11111L, "User 1");
        userManager.createUser(22222L, "User 2");

        Collection<User> allUsers = userManager.getAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    public void testUserIdsIncrement() {
        User user1 = userManager.createUser(11111L, "User 1");
        User user2 = userManager.createUser(22222L, "User 2");
        User user3 = userManager.createUser(33333L, "User 3");

        // Проверяем, что ID увеличиваются
        assertTrue(user2.getId() > user1.getId());
        assertTrue(user3.getId() > user2.getId());
    }
}