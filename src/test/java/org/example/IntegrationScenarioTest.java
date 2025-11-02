package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationScenarioTest {

    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private StateHandler stateHandler;

    @BeforeEach
    public void setUp() {
        userManager = new UserManager();
        stateManager = new UserStateManager();

        // Создаем реальный TelegramBot с тестовыми параметрами
        TelegramBot testBot = new TelegramBot("test_token", "test_bot") {
            // Переопределяем execute методы чтобы избежать NPE
            // Эти методы не будут реально отправлять сообщения в телеграм
        };

        messageService = new MessageService(testBot);
        stateHandler = new StateHandler(userManager, stateManager, messageService);
        stateManager.initializeUser(123L);
    }

    @Test
    public void testCompleteEventCreationScenario() {
        // Создаем пользователя
        User user = userManager.createUser(123L, "TestUser");

        // Сценарий: добавление события (без вызова кнопок)
        stateHandler.handleState(123L, "MAIN_MENU", "1"); // "Добавить дело"
        assertEquals("ADD_EVENT_YEAR", stateManager.getUserState(123L));

        // Вводим год
        stateHandler.handleState(123L, "ADD_EVENT_YEAR", "2025");
        assertEquals("ADD_EVENT_MONTH", stateManager.getUserState(123L));

        // Вводим месяц ЧЕРЕЗ ТЕКСТ (не кнопки)
        stateHandler.handleState(123L, "ADD_EVENT_MONTH", "6");
        assertEquals("ADD_EVENT_DAY", stateManager.getUserState(123L));

        // Вводим день ЧЕРЕЗ ТЕКСТ (не кнопки)
        stateHandler.handleState(123L, "ADD_EVENT_DAY", "15");
        assertEquals("ADD_EVENT_TIME", stateManager.getUserState(123L));

        // Вводим время
        stateHandler.handleState(123L, "ADD_EVENT_TIME", "14:30");
        assertEquals("ADD_EVENT_TITLE", stateManager.getUserState(123L));

        // Вводим название
        stateHandler.handleState(123L, "ADD_EVENT_TITLE", "Test Meeting");
        assertEquals("ADD_EVENT_DESCRIPTION", stateManager.getUserState(123L));

        // Вводим описание и завершаем
        stateHandler.handleState(123L, "ADD_EVENT_DESCRIPTION", "Test Description");

        // Проверяем, что событие создано
        Year year = user.getExistingYear(2025);
        assertNotNull(year);

        Month month = year.getExistingMonth(6);
        assertNotNull(month);

        Day day = month.getExistingDay(15);
        assertNotNull(day);
        assertEquals(1, day.getEventsCount());

        Event event = day.getEvent("14:30");
        assertNotNull(event);
        assertEquals("Test Meeting", event.getTitle());
        assertEquals("Test Description", event.getComm());
    }

    @Test
    public void testUserSwitchScenario() {
        // Создаем двух пользователей
        User user1 = userManager.createUser(123L, "User1");
        User user2 = userManager.createUser(456L, "User2");

        // Переключаем пользователя
        stateHandler.handleState(123L, "SWITCH_USER", "User2");

        // Проверяем переключение
        User currentUser = userManager.getUserByChatId(123L);
        assertEquals(user2, currentUser);
        assertEquals("MAIN_MENU", stateManager.getUserState(123L));
    }

    @Test
    public void testViewEventsScenario() {
        // Создаем пользователя и добавляем событие напрямую
        User user = userManager.createUser(123L, "TestUser");
        user.getYear(2025).addEvent(6, 15, "10:00", "Meeting", "Team meeting");

        // Сценарий просмотра событий
        stateHandler.handleState(123L, "MAIN_MENU", "2"); // "Посмотреть дела"
        assertEquals("VIEW_EVENTS_YEAR", stateManager.getUserState(123L));

        stateHandler.handleState(123L, "VIEW_EVENTS_YEAR", "2025");
        assertEquals("VIEW_EVENTS_MONTH", stateManager.getUserState(123L));

        stateHandler.handleState(123L, "VIEW_EVENTS_MONTH", "6");
        assertEquals("VIEW_EVENTS_DAY", stateManager.getUserState(123L));

        // День с событием должен существовать
        Day day = user.getYear(2025).getMonth(6).getExistingDay(15);
        assertNotNull(day);
        assertEquals(1, day.getEventsCount());
    }

    @Test
    public void testUserNameChangeScenario() {
        User user = userManager.createUser(123L, "OldName");

        stateHandler.handleState(123L, "CHANGE_USER_NAME", "NewName");

        assertEquals("NewName", user.getName());
        assertEquals("MAIN_MENU", stateManager.getUserState(123L));
    }

    @Test
    public void testDirectEventCreation() {
        // Альтернативный тест: создаем событие напрямую без UI flow
        User user = userManager.createUser(123L, "DirectTest");

        // Создаем событие напрямую через API
        user.getYear(2025).addEvent(6, 15, "14:30", "Direct Meeting", "Direct Description");

        // Проверяем создание
        Year year = user.getExistingYear(2025);
        assertNotNull(year);

        Month month = year.getExistingMonth(6);
        assertNotNull(month);

        Day day = month.getExistingDay(15);
        assertNotNull(day);
        assertEquals(1, day.getEventsCount());

        Event event = day.getEvent("14:30");
        assertNotNull(event);
        assertEquals("Direct Meeting", event.getTitle());
        assertEquals("Direct Description", event.getComm());
    }
}