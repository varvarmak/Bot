package org.example.suites;

import org.example.services.UserManager;
import org.example.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class ViewEventsTest {

    private UserManager userManager;
    private User testUser;
    private static final Long TEST_CHAT_ID = 300001L;
    private static final String TEST_USER_NAME = "ViewTestUser";

    @Before
    public void setUp() {
        System.out.println("\n  Настройка теста: Просмотр событий");
        userManager = new UserManager();
        testUser = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);

        // Создаем тестовые события
        createTestEvents();
    }

    private void createTestEvents() {
        // События на разные даты
        testUser.getYear(2025).addEvent(1, 10, "09:00", "Планерка", "Ежедневная планерка");
        testUser.getYear(2025).addEvent(1, 10, "14:00", "Обед", "Обед с коллегами");
        testUser.getYear(2025).addEvent(2, 14, "19:00", "Ужин", "Романтический ужин");
        testUser.getYear(2026).addEvent(3, 8, "10:00", "Конференция", "IT конференция");
    }

    @Test
    public void testViewEventsForDay() {
        System.out.println("    Тест: Просмотр событий за день");

        int year = 2025;
        int month = 1;
        int day = 10;

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);

        // Проверяем количество событий
        assertEquals("Должно быть 2 события", 2, dayObj.getEventsCount());

        // Получаем все события
        Event[] events = dayObj.getEvents();
        List<String> foundTitles = new ArrayList<>();

        for (Event event : events) {
            if (event != null) {
                foundTitles.add(event.getTitle());
                System.out.println("    Найдено: " + event.getTime() + " - " + event.getTitle());
            }
        }

        // Проверяем, что все события найдены
        assertTrue("Должна быть планерка", foundTitles.contains("Планерка"));
        assertTrue("Должен быть обед", foundTitles.contains("Обед"));

        System.out.println("    Найдено событий за день: " + foundTitles.size());
    }

    @Test
    public void testEmptyDay() {
        System.out.println("    Тест: Просмотр пустого дня");

        int year = 2025;
        int month = 1;
        int day = 1; // День без событий

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);

        assertEquals("День должен быть пустым", 0, dayObj.getEventsCount());

        Event[] events = dayObj.getEvents();
        boolean hasEvents = false;
        for (Event event : events) {
            if (event != null) {
                hasEvents = true;
                break;
            }
        }

        assertFalse("Не должно быть событий", hasEvents);

        System.out.println("    День без событий обработан корректно");
    }

    @Test
    public void testGetSpecificEvent() {
        System.out.println("    Тест: Получение конкретного события по времени");

        int year = 2025;
        int month = 2;
        int day = 14;
        String time = "19:00";

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);
        Event event = dayObj.getEvent(time);

        assertNotNull("Событие должно быть найдено", event);
        assertEquals("Ужин", event.getTitle());
        assertEquals("Романтический ужин", event.getComm());

        System.out.println("    Конкретное событие найдено: " + event.getTitle());
    }

    @Test
    public void testEventDetails() {
        System.out.println("   Тест: Проверка деталей события");

        int year = 2026;
        int month = 3;
        int day = 8;
        String time = "10:00";

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);
        Event event = dayObj.getEvent(time);

        // Проверяем все поля события
        assertEquals("Время должно совпадать", time, event.getTime());
        assertEquals("Заголовок должен совпадать", "Конференция", event.getTitle());
        assertEquals("Описание должно совпадать", "IT конференция", event.getComm());

        // Проверяем методы форматирования
        assertNotNull("getFormattedTime не должен возвращать null", event.getFormattedTime());
        assertNotNull("getFormattedDate не должен возвращать null", event.getFormattedDate());
        assertNotNull("formatEvent не должен возвращать null", event.formatEvent());

        System.out.println("    Детали события корректны:");
        System.out.println("      Время: " + event.getTime());
        System.out.println("      Заголовок: " + event.getTitle());
        System.out.println("      Описание: " + event.getComm());
    }

    @Test
    public void testDayInformation() {
        System.out.println("   ▶ Тест: Информация о дне");

        int year = 2025;
        int month = 1;
        int day = 10;

        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);

        // Проверяем методы Day
        assertEquals("Номер дня должен совпадать", day, dayObj.getDayNumber());

        String dayMessage = dayObj.messageDay();
        assertTrue("Сообщение должно содержать номер дня", dayMessage.contains("День " + day));
        assertTrue("Сообщение должно содержать количество дел", dayMessage.contains("дел"));

        String dayString = dayObj.toString();
        assertNotNull("toString не должен возвращать null", dayString);
        assertTrue("Должно содержать информацию о событиях",
                dayString.contains("Планерка") || dayString.contains("Обед"));

        System.out.println("    Информация о дне корректна");
        System.out.println("      " + dayMessage);
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}