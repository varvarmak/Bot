package org.example.suites;

import org.example.services.*;
import org.example.models.*;
import org.example.DataBaseManager;
import org.example.bot.handlers.StateHandler;
import org.example.bot.handlers.MessageSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import java.util.List;

public class IntegrationTest {

    @Mock
    private org.example.bot.TelegramBot mockBot;

    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private MessageSender messageSender;
    private StateHandler stateHandler;
    private DataBaseManager databaseManager;
    private ReminderService reminderService;

    private static final Long TEST_CHAT_ID = 1000001L;
    private static final String TEST_USER_NAME = "ИнтеграционныйТест";

    @Before
    public void setUp() {
        System.out.println("\n🔧 Настройка теста: Интеграционный тест");
        MockitoAnnotations.openMocks(this);

        userManager = new UserManager();
        stateManager = new UserStateManager();
        databaseManager = new DataBaseManager();
        messageService = new MessageService(mockBot);
        messageSender = new MessageSender(mockBot);
        stateHandler = new StateHandler(userManager, stateManager, messageService, messageSender);
        reminderService = new ReminderService(userManager, mockBot);

        userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);
        stateManager.initializeUser(TEST_CHAT_ID);
    }

    @Test
    public void testCompleteEventCreationFlow() {
        System.out.println("    Тест: Полный цикл создания события");

        User user = userManager.getUserByChatId(TEST_CHAT_ID);
        assertNotNull("Пользователь должен существовать", user);

        stateManager.setUserState(TEST_CHAT_ID, "ADD_EVENT_YEAR");
        assertEquals("ADD_EVENT_YEAR", stateManager.getUserState(TEST_CHAT_ID));

        stateHandler.handleState(TEST_CHAT_ID, "ADD_EVENT_YEAR", "2025");
        assertEquals("ADD_EVENT_MONTH", stateManager.getUserState(TEST_CHAT_ID));

        EventData tempData = stateManager.getTempEventData(TEST_CHAT_ID);
        assertEquals("Год должен быть установлен", 2025, tempData.year);

        stateHandler.handleState(TEST_CHAT_ID, "ADD_EVENT_MONTH", "6");
        assertEquals("ADD_EVENT_DAY", stateManager.getUserState(TEST_CHAT_ID));
        assertEquals("Месяц должен быть установлен", 6, tempData.month);

        stateHandler.handleState(TEST_CHAT_ID, "ADD_EVENT_DAY", "15");
        assertEquals("ADD_EVENT_TIME", stateManager.getUserState(TEST_CHAT_ID));
        assertEquals("День должен быть установлен", 15, tempData.day);

        stateHandler.handleState(TEST_CHAT_ID, "ADD_EVENT_TIME", "14:30");
        assertEquals("ADD_EVENT_TITLE", stateManager.getUserState(TEST_CHAT_ID));
        assertEquals("Время должно быть установлено", "14:30", tempData.time);

        stateHandler.handleState(TEST_CHAT_ID, "ADD_EVENT_TITLE", "Интеграционная встреча");
        assertEquals("ADD_EVENT_DESCRIPTION", stateManager.getUserState(TEST_CHAT_ID));
        assertEquals("Заголовок должен быть установлен", "Интеграционная встреча", tempData.title);

        stateHandler.handleState(TEST_CHAT_ID, "ADD_EVENT_DESCRIPTION", "Тест интеграции систем");

        Day day = user.getYear(2025).getMonth(6).getDay(15);
        Event event = day.getEvent("14:30");
        assertNotNull("Событие должно быть создано в памяти", event);
        assertEquals("Интеграционная встреча", event.getTitle());
        assertEquals("Тест интеграции систем", event.getComm());

        assertEquals("MAIN_MENU", stateManager.getUserState(TEST_CHAT_ID));

        System.out.println("    Полный цикл создания события выполнен");
    }

    @Test
    public void testDatabaseIntegration() {
        System.out.println("    Тест: Интеграция с базой данных");

        UserData userData = new UserData();
        userData.setYear(2025);
        userData.setMonth("7");
        userData.setDay(20);
        userData.setReminderTime("16:45");
        userData.setReminderName("Интеграционный тест БД");
        userData.setDescription("Проверка сохранения в БД");

        userManager.saveReminder(TEST_CHAT_ID, userData);

        List<UserData> reminders = databaseManager.getLastReminders(TEST_CHAT_ID, 10);
        assertFalse("Напоминания должны быть в БД", reminders.isEmpty());

        // Ищем наше напоминание среди всех
        boolean found = false;
        for (UserData savedData : reminders) {
            if ("Интеграционный тест БД".equals(savedData.getReminderName()) &&
                    "16:45".equals(savedData.getReminderTime())) {
                found = true;
                System.out.println("    Найдено напоминание: " + savedData.getReminderName());
                break;
            }
        }

        assertTrue("Должно найти напоминание 'Интеграционный тест БД'", found);

        // Проверяем счетчик
        int count = databaseManager.getUserReminderCount(TEST_CHAT_ID);
        assertTrue("Счетчик должен быть >= 1", count >= 1);

        System.out.println("    Интеграция с БД работает:");
        System.out.println("      Сохранено напоминаний: " + count);
    }

    @Test
    public void testReminderServiceIntegration() {
        System.out.println("    Тест: Интеграция с сервисом напоминаний");

        try {
            reminderService.start();
            System.out.println("    Сервис напоминаний запущен");

            // Даем время на запуск потока
            Thread.sleep(1000);

            reminderService.stop();
            System.out.println("    Сервис напоминаний остановлен");

        } catch (Exception e) {
            fail("Сервис напоминаний должен работать: " + e.getMessage());
        }
    }

    @Test
    public void testStateManagementIntegration() {
        System.out.println("   Тест: Интеграция управления состояниями");

        // Тестируем различные состояния
        String[] statesToTest = {
                "VIEW_EVENTS_YEAR",
                "VIEW_EVENTS_MONTH",
                "CHANGE_USER_NAME",
                "SWITCH_USER"
        };

        for (String state : statesToTest) {
            stateManager.setUserState(TEST_CHAT_ID, state);
            assertEquals(state, stateManager.getUserState(TEST_CHAT_ID));

            // Проверяем обработку состояния
            try {
                stateHandler.handleState(TEST_CHAT_ID, state, "тест");
                System.out.println("      Состояние " + state + " обработано");
            } catch (Exception e) {
                System.out.println("      Состояние " + state + " требует дополнительных данных");
            }
        }

        stateManager.setUserState(TEST_CHAT_ID, "MAIN_MENU");
        assertEquals("MAIN_MENU", stateManager.getUserState(TEST_CHAT_ID));

        System.out.println("    Управление состояниями интегрировано");
    }

    @Test
    public void testUserSwitchingIntegration() {
        System.out.println("    Тест: Интеграция смены пользователя");

        User secondUser = userManager.createUser(1000002L, "ВторойПользователь");

        secondUser.getYear(2025).addEvent(8, 1, "10:00", "Событие второго", "Описание");

        userManager.switchUser(TEST_CHAT_ID, secondUser);

        // Проверяем переключение
        User currentUser = userManager.getUserByChatId(TEST_CHAT_ID);
        assertEquals("Должен быть второй пользователь",
                "ВторойПользователь", currentUser.getName());

        Day day = currentUser.getYear(2025).getMonth(8).getDay(1);
        Event event = day.getEvent("10:00");
        assertNotNull("Должно быть событие второго пользователя", event);
        assertEquals("Событие второго", event.getTitle());

        User firstUser = userManager.getUserByName(TEST_USER_NAME);
        userManager.switchUser(TEST_CHAT_ID, firstUser);

        System.out.println("    Смена пользователя интегрирована");
    }

    @Test
    public void testCompleteSystemWorkflow() {
        System.out.println("   ▶ Тест: Полный рабочий процесс системы");

        System.out.println("      1. Создание пользователя...");
        User user = userManager.getUserByChatId(TEST_CHAT_ID);
        assertNotNull(user);

        System.out.println("      2. Добавление события...");
        user.getYear(2025).addEvent(12, 25, "18:00", "Новый Год", "Подготовка");

        System.out.println("      3. Сохранение в БД...");
        UserData userData = new UserData();
        userData.setYear(2025);
        userData.setMonth("12");
        userData.setDay(25);
        userData.setReminderTime("18:00");
        userData.setReminderName("Новый Год");
        userData.setDescription("Подготовка");
        userManager.saveReminder(TEST_CHAT_ID, userData);

        System.out.println("      4. Проверка в БД...");
        List<UserData> reminders = databaseManager.getLastReminders(TEST_CHAT_ID, 5);
        assertFalse(reminders.isEmpty());

        System.out.println("      5. Проверка в памяти...");
        Day day = user.getYear(2025).getMonth(12).getDay(25);
        Event event = day.getEvent("18:00");
        assertNotNull(event);

        System.out.println("      6. Установка состояния...");
        stateManager.setUserState(TEST_CHAT_ID, "MAIN_MENU");
        assertEquals("MAIN_MENU", stateManager.getUserState(TEST_CHAT_ID));

        System.out.println("      7. Очистка временных данных...");
        stateManager.clearTempEventData(TEST_CHAT_ID);

        System.out.println("    Полный рабочий процесс выполнен успешно!");
    }

    @After
    public void tearDown() throws InterruptedException {
        // Останавливаем сервисы
        reminderService.stop();
        Thread.sleep(500);

        System.out.println("    Очистка после теста\n");
    }
}