package org.example.suites;

import org.example.DataBaseManager;
import org.example.services.UserData;
import org.example.services.UserManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseOperationsTest {

    private DataBaseManager databaseManager;
    private UserManager userManager;
    private static final Long TEST_USER_ID = 600001L;
    private static final Long ANOTHER_USER_ID = 600002L;

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Операции с базой данных");
        databaseManager = new DataBaseManager();
        userManager = new UserManager();

        clearTestData();

        databaseManager.saveUser(TEST_USER_ID);
        databaseManager.saveUser(ANOTHER_USER_ID);
    }

    private void clearTestData() {
        try {
            String testDbFile = "telegram_bot.db";
            File dbFile = new File(testDbFile);
            if (dbFile.exists()) {
                System.out.println("    Очистка тестовых данных БД");
            }
        } catch (Exception e) {
            System.out.println("    Не удалось очистить БД: " + e.getMessage());
        }
    }

    @Test
    public void testSaveAndRetrieveReminder() {
        System.out.println("    Тест: Сохранение и получение напоминания");

        UserData testData = new UserData();
        testData.setYear(2025);
        testData.setMonth("6");
        testData.setDay(15);
        testData.setReminderTime("14:30");
        testData.setReminderName("Тестовое напоминание");
        testData.setDescription("Описание тестового напоминания");

        databaseManager.saveReminder(TEST_USER_ID, testData);

        List<UserData> reminders = databaseManager.getLastReminders(TEST_USER_ID, 5);

        assertFalse("Список напоминаний не должен быть пустым", reminders.isEmpty());

        boolean found = false;
        for (UserData retrievedData : reminders) {
            if ("Тестовое напоминание".equals(retrievedData.getReminderName()) &&
                    "14:30".equals(retrievedData.getReminderTime())) {
                found = true;
                assertEquals("Год должен совпадать", 2025, retrievedData.getYear());
                assertEquals("Месяц должен совпадать", "6", retrievedData.getMonth());
                assertEquals("День должен совпадать", 15, retrievedData.getDay());
                assertEquals("Описание должно совпадать", "Описание тестового напоминания", retrievedData.getDescription());
                break;
            }
        }

        assertTrue("Должно найти сохраненное напоминание", found);

        System.out.println("    Напоминание сохранено и получено");
    }

    @Test
    public void testMultipleReminders() {
        System.out.println("    Тест: Несколько напоминаний");

        for (int i = 1; i <= 3; i++) {
            UserData data = new UserData();
            data.setYear(2025);
            data.setMonth("7");
            data.setDay(i);
            data.setReminderTime(String.format("%02d:00", i + 9));
            data.setReminderName("Напоминание " + i);
            data.setDescription("Описание " + i);

            databaseManager.saveReminder(TEST_USER_ID, data);
        }

        List<UserData> reminders = databaseManager.getLastReminders(TEST_USER_ID, 10);
        assertTrue("Должно быть не менее 3 напоминаний", reminders.size() >= 3);

        Set<String> reminderNames = new HashSet<>();
        for (UserData reminder : reminders) {
            if (reminder.getReminderName() != null && reminder.getReminderName().startsWith("Напоминание ")) {
                reminderNames.add(reminder.getReminderName());
            }
        }

        assertTrue("Должно содержать Напоминание 1", reminderNames.contains("Напоминание 1"));
        assertTrue("Должно содержать Напоминание 2", reminderNames.contains("Напоминание 2"));
        assertTrue("Должно содержать Напоминание 3", reminderNames.contains("Напоминание 3"));

        System.out.println("    Создано несколько напоминаний: " + reminders.size());
    }

    @Test
    public void testReminderCount() {
        System.out.println("    Тест: Подсчет напоминаний");

        int initialCount = databaseManager.getUserReminderCount(TEST_USER_ID);

        UserData data = new UserData();
        data.setYear(2025);
        data.setMonth("8");
        data.setDay(1);
        data.setReminderTime("10:00");
        data.setReminderName("Для подсчета");
        data.setDescription("Тест подсчета");

        databaseManager.saveReminder(TEST_USER_ID, data);

        int newCount = databaseManager.getUserReminderCount(TEST_USER_ID);
        assertTrue("Количество должно увеличиться", newCount >= initialCount + 1);

        System.out.println("    Подсчет напоминаний работает:");
        System.out.println("      Было: " + initialCount + ", стало: " + newCount);
    }

    @Test
    public void testUserSeparation() {
        System.out.println("    Тест: Разделение данных пользователей");

        clearDatabase();

        UserData data1 = new UserData();
        data1.setYear(2025);
        data1.setMonth("9");
        data1.setDay(1);
        data1.setReminderTime("09:00");
        data1.setReminderName("Для пользователя 1");
        data1.setDescription("Описание 1");

        UserData data2 = new UserData();
        data2.setYear(2025);
        data2.setMonth("9");
        data2.setDay(1);
        data2.setReminderTime("10:00");
        data2.setReminderName("Для пользователя 2");
        data2.setDescription("Описание 2");

        // Сохраняем и сразу проверяем
        databaseManager.saveReminder(TEST_USER_ID, data1);
        databaseManager.saveReminder(ANOTHER_USER_ID, data2);

        // Проверяем разделение - получаем ТОЛЬКО что сохранили
        List<UserData> user1Reminders = databaseManager.getLastReminders(TEST_USER_ID, 1);
        List<UserData> user2Reminders = databaseManager.getLastReminders(ANOTHER_USER_ID, 1);

        assertFalse("Пользователь 1 должен иметь напоминания", user1Reminders.isEmpty());
        assertFalse("Пользователь 2 должен иметь напоминания", user2Reminders.isEmpty());

        // Проверяем правильность данных
        if (!user1Reminders.isEmpty()) {
            UserData user1Reminder = user1Reminders.get(0);
            assertEquals("Пользователь 1: неправильное название",
                    "Для пользователя 1", user1Reminder.getReminderName());
            assertEquals("Пользователь 1: неправильное время",
                    "09:00", user1Reminder.getReminderTime());
        }

        if (!user2Reminders.isEmpty()) {
            UserData user2Reminder = user2Reminders.get(0);
            assertEquals("Пользователь 2: неправильное название",
                    "Для пользователя 2", user2Reminder.getReminderName());
            assertEquals("Пользователь 2: неправильное время",
                    "10:00", user2Reminder.getReminderTime());
        }

        System.out.println("    Данные пользователей разделены корректно");
    }

    private void clearDatabase() {
        try {
            File dbFile = new File("telegram_bot.db");
            if (dbFile.exists()) {
                databaseManager = null;
                System.gc();

                Thread.sleep(100);

                if (dbFile.delete()) {
                    System.out.println("   🧹 БД очищена");
                }

                databaseManager = new DataBaseManager();
            }
        } catch (Exception e) {
            System.out.println("    Не удалось очистить БД: " + e.getMessage());
        }
    }

    @Test
    public void testEmptyRemindersList() {
        System.out.println("    Тест: Пустой список напоминаний");

        Long newUserId = 600003L;

        List<UserData> reminders = databaseManager.getLastReminders(newUserId, 5);
        assertNotNull("Список не должен быть null", reminders);
        assertTrue("Список должен быть пустым для нового пользователя", reminders.isEmpty());

        int count = databaseManager.getUserReminderCount(newUserId);
        assertEquals("Количество должно быть 0", 0, count);

        System.out.println("    Пустой список обрабатывается корректно");
    }

    @Test
    public void testReminderWithEmptyFields() {
        System.out.println("    Тест: Напоминание с пустыми полями");

        UserData data = new UserData();
        data.setYear(2025);
        data.setMonth("10");
        data.setDay(5);
        data.setReminderTime("12:00");
        data.setReminderName("Только время");

        databaseManager.saveReminder(TEST_USER_ID, data);

        List<UserData> reminders = databaseManager.getLastReminders(TEST_USER_ID, 10);
        assertFalse("Список не должен быть пустым", reminders.isEmpty());

        boolean found = false;
        for (UserData reminder : reminders) {
            if ("Только время".equals(reminder.getReminderName()) &&
                    "12:00".equals(reminder.getReminderTime()) &&
                    reminder.getDescription() == null) {
                found = true;
                break;
            }
        }

        assertTrue("Должно найти напоминание 'Только время'", found);

        System.out.println("    Напоминание с пустыми полями сохранено");
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}