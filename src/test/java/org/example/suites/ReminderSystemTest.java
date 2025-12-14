package org.example.suites;

import org.example.services.UserManager;
import org.example.services.ReminderService;
import org.example.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class ReminderSystemTest {

    @Mock
    private org.example.bot.TelegramBot mockBot;

    private UserManager userManager;
    private ReminderService reminderService;
    private User testUser;
    private static final Long TEST_CHAT_ID = 500001L;
    private static final String TEST_USER_NAME = "ReminderTestUser";

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Система напоминаний");
        MockitoAnnotations.openMocks(this);

        userManager = new UserManager();
        testUser = userManager.createUser(TEST_CHAT_ID, TEST_USER_NAME);
        reminderService = new ReminderService(userManager, mockBot);
    }

    @Test
    public void testEventRemindedFlag() {
        System.out.println("    Тест: Флаг напоминания события");

        int year = 2025;
        int month = 6;
        int day = 10;
        String time = "14:00";


        testUser.getYear(year).addEvent(month, day, time, "Тест напоминания", "Описание");


        Day dayObj = testUser.getYear(year).getMonth(month).getDay(day);
        Event event = dayObj.getEvent(time);

        assertNotNull(event);
        assertFalse("Событие не должно быть напомнено изначально", event.isReminded());

        event.setReminded(true);
        assertTrue("Событие должно быть помечено как напомненное", event.isReminded());

        event.setReminded(false);
        assertFalse("Событие должно снова быть не напомненным", event.isReminded());

        System.out.println("    Флаг напоминания работает корректно");
    }

    @Test
    public void testReminderServiceStartStop() {
        System.out.println("    Тест: Запуск и остановка сервиса напоминаний");

        try {
            reminderService.start();
            System.out.println("    Сервис напоминаний запущен");

            // Даем сервису время на запуск
            Thread.sleep(1000);

            reminderService.stop();
            System.out.println("    Сервис напоминаний остановлен");

        } catch (Exception e) {
            fail("Сервис должен запускаться и останавливаться без ошибок: " + e.getMessage());
        }
    }

    @Test
    public void testEventDateTime() {
        System.out.println("   ▶ Тест: Дата и время события");

        LocalDateTime eventDateTime = LocalDateTime.of(2025, 12, 25, 18, 30);
        Event event = new Event(eventDateTime, "Новогодний ужин", "Семейный ужин");

        // Проверяем поля
        assertEquals("18:30", event.getTime());
        assertEquals("Новогодний ужин", event.getTitle());
        assertEquals("Семейный ужин", event.getComm());
        assertEquals(eventDateTime, event.getEventDateTime());

        // Проверяем форматирование
        assertEquals("18:30", event.getFormattedTime());
        assertEquals("25.12.2025", event.getFormattedDate());

        System.out.println("    Дата и время события корректны");
    }

    @Test
    public void testEventSetters() {
        System.out.println("    Тест: Сеттеры события");

        Event event = new Event("10:00", "Заголовок", "Описание");

        // Меняем значения через сеттеры
        event.setTime("11:00");
        event.setTitle("Новый заголовок");
        event.setComm("Новое описание");
        event.setReminded(true);

        LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);
        event.setEventDateTime(newDateTime);

        // Проверяем изменения
        assertEquals("11:00", event.getTime());
        assertEquals("Новый заголовок", event.getTitle());
        assertEquals("Новое описание", event.getComm());
        assertTrue(event.isReminded());
        assertEquals(newDateTime, event.getEventDateTime());

        System.out.println("    Сеттеры события работают корректно");
    }

    @Test
    public void testCheckUserReminders() {
        System.out.println("    Тест: Проверка напоминаний пользователя");

        // Создаем событие в будущем
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(5);
        int year = futureTime.getYear();
        int month = futureTime.getMonthValue();
        int day = futureTime.getDayOfMonth();
        String time = String.format("%02d:%02d", futureTime.getHour(), futureTime.getMinute());

        testUser.getYear(year).addEvent(month, day, time, "Будущее событие", "Тест");

        try {

            System.out.println("    Прямой тест приватных методов невозможен");
            System.out.println("    Структура для напоминаний создана");

        } catch (Exception e) {
            fail("Не должно быть исключений: " + e.getMessage());
        }
    }

    @Test
    public void testEventMessageFormat() {
        System.out.println("    Тест: Форматирование сообщений события");

        Event event = new Event("14:30", "Встреча", "Важная встреча");

        String message = event.massageEvent();
        assertNotNull(message);
        assertTrue(message.contains("14:30"));
        assertTrue(message.contains("Встреча"));
        assertTrue(message.contains("Важная встреча"));

        String formatted = event.formatEvent();
        assertNotNull(formatted);

        System.out.println("    Форматирование сообщений корректно:");
        System.out.println("      " + message);
        System.out.println("      " + formatted);
    }

    @After
    public void tearDown() throws InterruptedException {
        // Останавливаем сервис, если он запущен
        reminderService.stop();
        Thread.sleep(500); // Даем время на остановку

        System.out.println("    Очистка после теста\n");
    }
}