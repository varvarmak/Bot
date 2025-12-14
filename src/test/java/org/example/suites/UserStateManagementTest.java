package org.example.suites;

import org.example.services.UserStateManager;
import org.example.models.EventData;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class UserStateManagementTest {

    private UserStateManager stateManager;
    private static final Long TEST_CHAT_ID = 400001L;
    private static final Long ANOTHER_CHAT_ID = 400002L;

    @Before
    public void setUp() {
        System.out.println("\nНастройка теста: Управление состояниями");
        stateManager = new UserStateManager();
    }

    @Test
    public void testInitialState() {
        System.out.println("    Тест: Начальное состояние");

        String state = stateManager.getUserState(TEST_CHAT_ID);
        assertEquals("Начальное состояние должно быть MAIN_MENU",
                "MAIN_MENU", state);

        System.out.println("    Начальное состояние корректно: " + state);
    }

    @Test
    public void testSetAndGetState() {
        System.out.println("   Тест: Установка и получение состояния");

        // Тестируем различные состояния
        String[] testStates = {
                "ADD_EVENT_YEAR",
                "ADD_EVENT_MONTH",
                "ADD_EVENT_DAY",
                "VIEW_EVENTS_YEAR",
                "CHANGE_USER_NAME",
                "SWITCH_USER"
        };

        for (String testState : testStates) {
            stateManager.setUserState(TEST_CHAT_ID, testState);
            String retrievedState = stateManager.getUserState(TEST_CHAT_ID);
            assertEquals("Состояние должно быть установлено корректно",
                    testState, retrievedState);
        }

        System.out.println("    Все состояния установлены и получены корректно");
    }

    @Test
    public void testMultipleUsersStates() {
        System.out.println("   Тест: Состояния нескольких пользователей");

        // Устанавливаем разные состояния для разных пользователей
        stateManager.setUserState(TEST_CHAT_ID, "ADD_EVENT_YEAR");
        stateManager.setUserState(ANOTHER_CHAT_ID, "VIEW_EVENTS_YEAR");

        // Проверяем, что состояния не пересекаются
        assertEquals("ADD_EVENT_YEAR", stateManager.getUserState(TEST_CHAT_ID));
        assertEquals("VIEW_EVENTS_YEAR", stateManager.getUserState(ANOTHER_CHAT_ID));

        // Меняем состояния
        stateManager.setUserState(TEST_CHAT_ID, "ADD_EVENT_MONTH");
        stateManager.setUserState(ANOTHER_CHAT_ID, "ADD_EVENT_DAY");

        // Проверяем изменения
        assertEquals("ADD_EVENT_MONTH", stateManager.getUserState(TEST_CHAT_ID));
        assertEquals("ADD_EVENT_DAY", stateManager.getUserState(ANOTHER_CHAT_ID));

        System.out.println("    Состояния нескольких пользователей управляются независимо");
    }

    @Test
    public void testInitializeUser() {
        System.out.println("    Тест: Инициализация пользователя");

        stateManager.initializeUser(TEST_CHAT_ID);

        String state = stateManager.getUserState(TEST_CHAT_ID);
        assertEquals("После инициализации состояние должно быть MAIN_MENU",
                "MAIN_MENU", state);

        EventData eventData = stateManager.getTempEventData(TEST_CHAT_ID);
        assertNotNull("После инициализации должны быть временные данные", eventData);

        System.out.println("    Пользователь успешно инициализирован");
    }

    @Test
    public void testTempEventData() {
        System.out.println("    Тест: Временные данные события");

        EventData eventData = stateManager.getTempEventData(TEST_CHAT_ID);
        assertNotNull("Должны быть созданы временные данные", eventData);

        // Заполняем данные
        eventData.year = 2025;
        eventData.month = 12;
        eventData.day = 25;
        eventData.time = "14:30";
        eventData.title = "Рождество";

        // Получаем данные снова (должны быть те же)
        EventData retrievedData = stateManager.getTempEventData(TEST_CHAT_ID);
        assertEquals("Год должен сохраниться", 2025, retrievedData.year);
        assertEquals("Месяц должен сохраниться", 12, retrievedData.month);
        assertEquals("День должен сохраниться", 25, retrievedData.day);
        assertEquals("Время должно сохраниться", "14:30", retrievedData.time);
        assertEquals("Заголовок должен сохраниться", "Рождество", retrievedData.title);

        System.out.println("    Временные данные сохраняются корректно");
    }

    @Test
    public void testClearTempEventData() {
        System.out.println("    Тест: Очистка временных данных");

        // Создаем и заполняем данные
        EventData eventData = stateManager.getTempEventData(TEST_CHAT_ID);
        eventData.year = 2025;
        eventData.month = 6;

        // Очищаем
        stateManager.clearTempEventData(TEST_CHAT_ID);

        // Получаем новые данные (должны быть пустыми)
        EventData newData = stateManager.getTempEventData(TEST_CHAT_ID);
        assertEquals("Год должен быть 0 после очистки", 0, newData.year);
        assertEquals("Месяц должен быть 0 после очистки", 0, newData.month);

        System.out.println("    Временные данные успешно очищены");
    }

    @Test
    public void testStatePersistence() {
        System.out.println("   ▶ Тест: Сохранение состояния");

        // Устанавливаем сложное состояние
        stateManager.setUserState(TEST_CHAT_ID, "ADD_EVENT_DESCRIPTION");

        // Заполняем временные данные
        EventData eventData = stateManager.getTempEventData(TEST_CHAT_ID);
        eventData.year = 2026;
        eventData.month = 3;
        eventData.day = 8;
        eventData.time = "15:45";
        eventData.title = "Важная встреча";

        // Получаем новый экземпляр менеджера (симулируем новый запуск)
        UserStateManager newManager = new UserStateManager();

        // Проверяем, что состояние не сохраняется между экземплярами
        // (это нормально для текущей реализации в памяти)
        String state = newManager.getUserState(TEST_CHAT_ID);
        assertEquals("MAIN_MENU", state);

        System.out.println("    Состояния не сохраняются между экземплярами (ожидаемо)");
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}