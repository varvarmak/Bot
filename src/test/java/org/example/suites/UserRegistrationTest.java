package org.example.suites;

import org.example.services.UserManager;
import org.example.models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class UserRegistrationTest {

    private UserManager userManager;
    private static final Long TEST_CHAT_ID = 100001L;
    private static final String TEST_USER_NAME = "TestUserReg";

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Регистрация пользователя");
        userManager = new UserManager();
    }

    @Test
    public void testUserCreation() {
        System.out.println("    Тест: Создание нового пользователя");

        // Проверяем, что пользователь не существует
        assertFalse("Пользователь не должен существовать до создания",
                userManager.userExists(TEST_CHAT_ID));

        // Создаем пользователя
        User user = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);

        // Проверяем создание
        assertNotNull("Пользователь должен быть создан", user);
        assertEquals("Имя пользователя должно совпадать",
                TEST_USER_NAME, user.getName());
        assertTrue("Пользователь должен существовать после создания",
                userManager.userExists(TEST_CHAT_ID));

        System.out.println("    Пользователь создан: " + user.getName());
    }

    @Test
    public void testGetUserByChatId() {
        System.out.println("   Тест: Получение пользователя по chatId");

        // Создаем пользователя
        User createdUser = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);

        // Получаем пользователя
        User retrievedUser = userManager.getUserByChatId(TEST_CHAT_ID);

        // Проверяем
        assertNotNull("Пользователь должен быть найден", retrievedUser);
        assertEquals("ID пользователей должны совпадать",
                createdUser.getId(), retrievedUser.getId());
        assertEquals("Имена пользователей должны совпадать",
                TEST_USER_NAME, retrievedUser.getName());

        System.out.println("    Пользователь найден по chatId: " + retrievedUser.getName());
    }

    @Test
    public void testGetUserByName() {
        System.out.println("   Тест: Получение пользователя по имени");

        // Создаем пользователя
        User createdUser = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);

        // Получаем пользователя по имени
        User retrievedUser = userManager.getUserByName(TEST_USER_NAME);

        // Проверяем
        assertNotNull("Пользователь должен быть найден по имени", retrievedUser);
        assertEquals("Должен быть найден правильный пользователь",
                createdUser.getId(), retrievedUser.getId());

        // Проверяем регистронезависимость
        User lowerCaseUser = userManager.getUserByName(TEST_USER_NAME.toLowerCase());
        assertNotNull("Поиск должен быть регистронезависимым", lowerCaseUser);
        assertEquals(createdUser.getId(), lowerCaseUser.getId());

        System.out.println("    Пользователь найден по имени: " + retrievedUser.getName());
    }

    @Test
    public void testMultipleUserCreation() {
        System.out.println("   Тест: Создание нескольких пользователей");

        Long[] chatIds = {100002L, 100003L, 100004L};
        String[] names = {"User1", "User2", "User3"};

        for (int i = 0; i < chatIds.length; i++) {
            User user = userManager.createUser(chatIds[i], names[i]);
            assertNotNull("Пользователь " + names[i] + " должен быть создан", user);
            assertTrue("Пользователь должен существовать", userManager.userExists(chatIds[i]));

            User retrieved = userManager.getUserByChatId(chatIds[i]);
            assertEquals("Имена должны совпадать", names[i], retrieved.getName());
        }

        System.out.println("    Создано " + chatIds.length + " пользователей");
    }

    @After
    public void tearDown() {
        System.out.println("   🧹 Очистка после теста\n");
    }
}