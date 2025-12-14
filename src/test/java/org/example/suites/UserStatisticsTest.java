package org.example.suites;

import org.example.services.UserManager;
import org.example.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class UserStatisticsTest {

    private UserManager userManager;
    private User testUser;
    private static final Long TEST_CHAT_ID = 900001L;
    private static final String TEST_USER_NAME = "StatsUser";

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Статистика пользователя");
        userManager = new UserManager();
        testUser = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);

        // Создаем тестовые события для статистики
        createTestEvents();
    }

    private void createTestEvents() {
        // События на 2025 год
        testUser.getYear(2025).addEvent(1, 10, "09:00", "Событие 1.2025", "Описание");
        testUser.getYear(2025).addEvent(1, 15, "14:00", "Событие 2.2025", "Описание");
        testUser.getYear(2025).addEvent(2, 20, "16:00", "Событие 3.2025", "Описание");

        // События на 2026 год
        testUser.getYear(2026).addEvent(3, 5, "10:00", "Событие 1.2026", "Описание");
        testUser.getYear(2026).addEvent(3, 10, "11:00", "Событие 2.2026", "Описание");
        testUser.getYear(2026).addEvent(4, 15, "12:00", "Событие 3.2026", "Описание");
        testUser.getYear(2026).addEvent(4, 20, "13:00", "Событие 4.2026", "Описание");

        // Событие на 2027 год
        testUser.getYear(2027).addEvent(5, 25, "14:00", "Событие 1.2027", "Описание");
    }

    @Test
    public void testTotalEventsCount() {
        System.out.println("    Тест: Общее количество событий");

        int totalEvents = testUser.getTotalEvents();

        // 3 в 2025 + 4 в 2026 + 1 в 2027 = 8 событий
        assertEquals("Должно быть 8 событий", 8, totalEvents);

        System.out.println("    Общее количество событий: " + totalEvents);
    }

    @Test
    public void testYearStatistics() {
        System.out.println("    Тест: Статистика по годам");

        // Проверяем 2025 год
        Year year2025 = testUser.getExistingYear(2025);
        assertNotNull("2025 год должен существовать", year2025);
        assertEquals("В 2025 должно быть 3 события", 3, year2025.getTotalEvents());

        // Проверяем 2026 год
        Year year2026 = testUser.getExistingYear(2026);
        assertNotNull("2026 год должен существовать", year2026);
        assertEquals("В 2026 должно быть 4 события", 4, year2026.getTotalEvents());

        // Проверяем 2027 год
        Year year2027 = testUser.getExistingYear(2027);
        assertNotNull("2027 год должен существовать", year2027);
        assertEquals("В 2027 должно быть 1 событие", 1, year2027.getTotalEvents());

        // Проверяем несуществующий год
        Year year2024 = testUser.getExistingYear(2024);
        assertNull("2024 год не должен существовать (до начального года)", year2024);

        System.out.println("    Статистика по годам:");
        System.out.println("      2025: " + year2025.getTotalEvents() + " событий");
        System.out.println("      2026: " + year2026.getTotalEvents() + " событий");
        System.out.println("      2027: " + year2027.getTotalEvents() + " событий");
    }

    @Test
    public void testMonthStatistics() {
        System.out.println("    Тест: Статистика по месяцам");

        Year year2025 = testUser.getYear(2025);

        // Январь 2025 - 2 события (10 и 15 числа)
        Month january = year2025.getExistingMonth(1);
        assertNotNull("Январь должен существовать", january);
        assertEquals("В январе должно быть 2 события", 2, january.getTotalEvents());

        // Февраль 2025 - 1 событие
        Month february = year2025.getExistingMonth(2);
        assertNotNull("Февраль должен существовать", february);
        assertEquals("В феврале должно быть 1 событие", 1, february.getTotalEvents());

        // Март 2025 - не должен существовать
        Month march = year2025.getExistingMonth(3);
        assertNull("Март не должен существовать (нет событий)", march);

        System.out.println("    Статистика по месяцам:");
        System.out.println("      Январь 2025: " + january.getTotalEvents() + " событий");
        System.out.println("      Февраль 2025: " + february.getTotalEvents() + " событий");
    }

    @Test
    public void testDayStatistics() {
        System.out.println("   ▶ Тест: Статистика по дням");

        Month january = testUser.getYear(2025).getMonth(1);

        // 10 января - 1 событие
        Day day10 = january.getExistingDay(10);
        assertNotNull("10 января должен существовать", day10);
        assertEquals("10 января должно быть 1 событие", 1, day10.getEventsCount());

        // 15 января - 1 событие
        Day day15 = january.getExistingDay(15);
        assertNotNull("15 января должен существовать", day15);
        assertEquals("15 января должно быть 1 событие", 1, day15.getEventsCount());

        // 1 января - не должен существовать
        Day day1 = january.getExistingDay(1);
        assertNull("1 января не должен существовать (нет событий)", day1);

        System.out.println("    Статистика по дням:");
        System.out.println("      10 января: " + day10.getEventsCount() + " событий");
        System.out.println("      15 января: " + day15.getEventsCount() + " событий");
    }

    @Test
    public void testEmptyUserStatistics() {
        System.out.println("    Тест: Статистика пустого пользователя");

        User emptyUser = userManager.createUser(900002L, "ПустойПользователь");

        assertEquals("У пустого пользователя должно быть 0 событий",
                0, emptyUser.getTotalEvents());

        // Проверяем несуществующий год
        Year year2025 = emptyUser.getExistingYear(2025);
        assertNull("Год не должен существовать у пустого пользователя", year2025);

        // Но мы можем его создать
        Year newYear = emptyUser.getYear(2025);
        assertNotNull("Год должен создаться при обращении", newYear);
        assertEquals("У нового года должно быть 0 событий", 0, newYear.getTotalEvents());

        System.out.println("    Статистика пустого пользователя корректна");
    }

    @Test
    public void testAddEventAndUpdateStatistics() {
        System.out.println("    Тест: Добавление события и обновление статистики");

        int initialTotal = testUser.getTotalEvents();

        // Добавляем новое событие
        testUser.getYear(2025).addEvent(6, 30, "18:00", "Новое событие", "Добавлено для теста");

        // Проверяем обновление статистики
        int newTotal = testUser.getTotalEvents();
        assertEquals("Общее количество должно увеличиться на 1",
                initialTotal + 1, newTotal);

        // Проверяем статистику года
        Year year2025 = testUser.getExistingYear(2025);
        assertEquals("В 2025 году должно быть 4 события", 4, year2025.getTotalEvents());

        // Проверяем статистику месяца
        Month june = year2025.getExistingMonth(6);
        assertNotNull("Июнь должен существовать", june);
        assertEquals("В июне должно быть 1 событие", 1, june.getTotalEvents());

        System.out.println("    Статистика обновляется при добавлении событий:");
        System.out.println("      Было: " + initialTotal + " событий");
        System.out.println("      Стало: " + newTotal + " событий");
    }

    @Test
    public void testUserInfo() {
        System.out.println("    Тест: Информация о пользователе");

        assertEquals("Имя должно совпадать", TEST_USER_NAME, testUser.getName());
        assertTrue("ID должен быть положительным", testUser.getId() > 0);

        // Создаем еще одного пользователя для сравнения
        User anotherUser = userManager.createUser(900003L, "ДругойПользователь");
        assertNotEquals("ID пользователей должны различаться",
                testUser.getId(), anotherUser.getId());

        System.out.println("    Информация о пользователе:");
        System.out.println("      Имя: " + testUser.getName());
        System.out.println("      ID: " + testUser.getId());
        System.out.println("      Всего событий: " + testUser.getTotalEvents());
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}