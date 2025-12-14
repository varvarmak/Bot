package org.example.suites;

import org.example.services.UserManager;
import org.example.models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.util.Collection;

public class UserSwitchingTest {

    private UserManager userManager;
    private static final Long CHAT_ID = 700001L;
    private static final String[] USER_NAMES = {"Алиса", "Боб", "Карл"};
    private static final Long[] CHAT_IDS = {700002L, 700003L, 700004L};

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Смена пользователя");
        userManager = new UserManager();

        // Создаем нескольких пользователей
        for (int i = 0; i < USER_NAMES.length; i++) {
            userManager.createUser(CHAT_IDS[i], USER_NAMES[i]);
        }
    }

    @Test
    public void testSwitchUser() {
        System.out.println("    Тест: Переключение между пользователями");

        // Первоначально связываем чат с первым пользователем
        User firstUser = userManager.getUserByName(USER_NAMES[0]);
        userManager.switchUser(CHAT_ID, firstUser);

        // Проверяем текущего пользователя
        User currentUser = userManager.getUserByChatId(CHAT_ID);
        assertEquals("Должен быть первый пользователь",
                USER_NAMES[0], currentUser.getName());

        // Переключаем на второго пользователя
        User secondUser = userManager.getUserByName(USER_NAMES[1]);
        userManager.switchUser(CHAT_ID, secondUser);

        // Проверяем переключение
        currentUser = userManager.getUserByChatId(CHAT_ID);
        assertEquals("Должен быть второй пользователь после переключения",
                USER_NAMES[1], currentUser.getName());

        System.out.println("    Переключение выполнено:");
        System.out.println("      Было: " + USER_NAMES[0]);
        System.out.println("      Стало: " + USER_NAMES[1]);
    }

    @Test
    public void testGetAvailableUsers() {
        System.out.println("    Тест: Получение списка доступных пользователей");

        String usersList = userManager.getAvailableUsersList();

        assertNotNull("Список пользователей не должен быть null", usersList);
        assertFalse("Список не должен быть пустым", usersList.isEmpty());

        // Проверяем, что все созданные пользователи есть в списке
        for (String userName : USER_NAMES) {
            assertTrue("Пользователь " + userName + " должен быть в списке",
                    usersList.contains(userName));
        }

        System.out.println("    Список пользователей получен:");
        System.out.println(usersList);
    }

    @Test
    public void testGetAllUsersCollection() {
        System.out.println("    Тест: Получение коллекции всех пользователей");

        Collection<User> allUsers = userManager.getAllUsers();

        assertNotNull("Коллекция не должна быть null", allUsers);
        assertEquals("Должно быть " + USER_NAMES.length + " пользователей",
                USER_NAMES.length, allUsers.size());

        // Проверяем, что все пользователи присутствуют
        for (User user : allUsers) {
            boolean found = false;
            for (String userName : USER_NAMES) {
                if (user.getName().equals(userName)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Пользователь " + user.getName() + " должен быть создан", found);
        }

        System.out.println("    Коллекция всех пользователей получена: " + allUsers.size() + " пользователей");
    }

    @Test
    public void testGetChatIdByUser() {
        System.out.println("    Тест: Получение chatId по пользователю");

        // Создаем пользователя и связываем с чатом
        User testUser = userManager.createUser(CHAT_ID, "ТестовыйПользователь");

        // Получаем chatId по пользователю
        Long retrievedChatId = userManager.getChatIdByUser(testUser);

        assertNotNull("ChatId должен быть найден", retrievedChatId);
        assertEquals("ChatId должен совпадать", CHAT_ID, retrievedChatId);

        System.out.println("    ChatId найден по пользователю: " + retrievedChatId);
    }

    @Test
    public void testUserEquality() {
        System.out.println("   Тест: Равенство пользователей");

        User user1 = userManager.createUser(700005L, "Дубликат");
        User user2 = userManager.getUserByName("Дубликат");

        // Проверяем equals и hashCode
        assertEquals("Пользователи должны быть равны по equals", user1, user2);
        assertEquals("Хэш-коды должны совпадать", user1.hashCode(), user2.hashCode());

        // Проверяем с разными пользователями
        User differentUser = userManager.createUser(700006L, "Другой");
        assertNotEquals("Разные пользователи не должны быть равны", user1, differentUser);

        System.out.println("   Равенство пользователей работает корректно");
    }

    @Test
    public void testUpdateUserName() {
        System.out.println("    Тест: Обновление имени пользователя");

        User user = userManager.createUser(700007L, "СтароеИмя");
        String oldName = user.getName();
        String newName = "НовоеИмя";

        // Обновляем имя
        userManager.updateUserName(user, oldName, newName);

        // Проверяем обновление
        assertEquals("Имя должно быть обновлено", newName, user.getName());

        // Проверяем поиск по старому имени
        User byOldName = userManager.getUserByName(oldName);
        assertNull("Не должен находиться по старому имени", byOldName);

        // Проверяем поиск по новому имени
        User byNewName = userManager.getUserByName(newName);
        assertNotNull("Должен находиться по новому имени", byNewName);
        assertEquals(user.getId(), byNewName.getId());

        System.out.println("    Имя пользователя обновлено:");
        System.out.println("      Было: " + oldName);
        System.out.println("      Стало: " + newName);
    }

    @Test
    public void testMultipleChatsSameUser() {
        System.out.println("   Тест: Один пользователь в нескольких чатах");

        User sharedUser = userManager.createUser(700008L, "ОбщийПользователь");

        // Связываем одного пользователя с несколькими чатами
        Long[] chatIds = {700009L, 700010L, 700011L};
        for (Long chatId : chatIds) {
            userManager.switchUser(chatId, sharedUser);
        }

        // Проверяем, что везде тот же пользователь
        for (Long chatId : chatIds) {
            User retrieved = userManager.getUserByChatId(chatId);
            assertEquals("Должен быть общий пользователь",
                    sharedUser.getId(), retrieved.getId());
        }

        System.out.println("    Один пользователь может быть в нескольких чатах");
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}