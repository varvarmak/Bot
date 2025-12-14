package org.example.suites;

import org.example.services.UserManager;
import org.example.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class CreateEventTest {

    private UserManager userManager;
    private User testUser;
    private static final Long TEST_CHAT_ID = 200001L;
    private static final String TEST_USER_NAME = "EventTestUser";

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Создание события");
        userManager = new UserManager();
        testUser = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);
    }

    @Test
    public void testSimpleEventCreation() {
        System.out.println("    Тест: Простое создание события");

        int year = 2025;
        int month = 6;
        int day = 15;
        String time = "14:30";
        String title = "Тестовая встреча";
        String description = "Описание тестовой встречи";

        testUser.getYear(year).addEvent(month, day, time, title, description);

        Month monthObj = testUser.getYear(year).getExistingMonth(month);
        assertNotNull("Месяц должен существовать", monthObj);

        Day dayObj = monthObj.getExistingDay(day);
        assertNotNull("День должен существовать", dayObj);

        assertEquals("Должно быть 1 событие", 1, dayObj.getEventsCount());

        Event event = dayObj.getEvent(time);
        assertNotNull("Событие должно существовать", event);
        assertEquals("Заголовок должен совпадать", title, event.getTitle());
        assertEquals("Описание должно совпадать", description, event.getComm());

        System.out.println("    Событие создано: " + title + " в " + time);
    }

    @Test
    public void testMultipleEventsSameDay() {
        System.out.println("   ▶ Тест: Несколько событий в один день");

        int year = 2025;
        int month = 7;
        int day = 20;

        String[][] events = {
                {"09:00", "Утренняя встреча", "Совещание по проекту"},
                {"14:00", "Обед", "Встреча с коллегами"},
                {"18:30", "Тренировка", "Спортзал"}
        };

        for (String[] eventData : events) {
            testUser.getYear(year).addEvent(month, day, eventData[0], eventData[1], eventData[2]);
        }

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);
        assertEquals("Должно быть 3 события", 3, dayObj.getEventsCount());

        for (String[] eventData : events) {
            Event event = dayObj.getEvent(eventData[0]);
            assertNotNull("Событие " + eventData[1] + " должно существовать", event);
            assertEquals(eventData[1], event.getTitle());
            assertEquals(eventData[2], event.getComm());
        }

        System.out.println("    Создано 3 события в один день");
    }

    @Test
    public void testEventsDifferentHours() {
        System.out.println("    Тест: События в разные часы");

        int year = 2025;
        int month = 8;
        int day = 10;

        for (int hour = 8; hour <= 18; hour += 2) {
            String time = String.format("%02d:00", hour);
            String title = "Событие в " + time;
            testUser.getYear(year).addEvent(month, day, time, title, "Описание");
        }

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);
        int expectedCount = 6;
        assertEquals("Должно быть " + expectedCount + " событий", expectedCount, dayObj.getEventsCount());

        assertNotNull("Событие в 10:00 должно существовать", dayObj.getEvent("10:00"));
        assertNotNull("Событие в 16:00 должно существовать", dayObj.getEvent("16:00"));

        System.out.println("    Создано событий в разные часы: " + expectedCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTime() {
        System.out.println("    Тест: Неверное время (ожидается исключение)");

        testUser.getYear(2025).addEvent(1, 1, "25:00", "Неверное время", "Описание");
    }

    @Test
    public void testEventWithEmptyDescription() {
        System.out.println("    Тест: Событие с пустым описанием");

        int year = 2025;
        int month = 9;
        int day = 5;
        String time = "12:00";
        String title = "Событие без описания";

        testUser.getYear(year).addEvent(month, day, time, title, "");

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);
        Event event = dayObj.getEvent(time);

        assertNotNull(event);
        assertEquals("", event.getComm());

        System.out.println("    Событие с пустым описанием создано");
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}