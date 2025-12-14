package org.example.suites;

import org.example.models.Month;
import org.example.models.Day;
import org.example.models.Year;
import org.example.models.User;
import org.example.utils.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class DataValidationTest {

    @Before
    public void setUp() {
        System.out.println("\n Настройка теста: Валидация данных");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMonthCreation() {
        System.out.println("    Тест: Создание неверного месяца (ожидается исключение)");

        Month invalidMonth = new Month(13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDayAccess() {
        System.out.println("   Тест: Доступ к неверному дню (ожидается исключение)");

        Month january = new Month(1);
        Day invalidDay = january.getDay(32);
    }

    @Test
    public void testValidTimeFormat() {
        System.out.println("   ▶ Тест: Валидация формата времени");

        assertTrue("00:00 должен быть валидным", DateTimeUtils.isValidTime("00:00"));
        assertTrue("12:30 должен быть валидным", DateTimeUtils.isValidTime("12:30"));
        assertTrue("23:59 должен быть валидным", DateTimeUtils.isValidTime("23:59"));
        assertTrue("09:05 должен быть валидным", DateTimeUtils.isValidTime("09:05"));

        assertFalse("24:00 не должен быть валидным", DateTimeUtils.isValidTime("24:00"));
        assertFalse("12:60 не должен быть валидным", DateTimeUtils.isValidTime("12:60"));
        assertFalse("25:30 не должен быть валидным", DateTimeUtils.isValidTime("25:30"));
        assertFalse("12:99 не должен быть валидным", DateTimeUtils.isValidTime("12:99"));
        assertFalse("abc:de не должен быть валидным", DateTimeUtils.isValidTime("abc:de"));
        assertFalse("12-30 не должен быть валидным", DateTimeUtils.isValidTime("12-30"));
        assertFalse("12345 не должен быть валидным", DateTimeUtils.isValidTime("12345"));

        System.out.println("    Формат времени валидируется корректно");
    }

    @Test
    public void testValidDate() {
        System.out.println("    Тест: Валидация даты");

        // Корректные даты
        assertTrue("2025-01-01 должна быть валидной",
                DateTimeUtils.isValidDate(2025, 1, 1));
        assertTrue("2025-12-31 должна быть валидной",
                DateTimeUtils.isValidDate(2025, 12, 31));
        assertTrue("2024-02-29 должна быть валидной (високосный)",
                DateTimeUtils.isValidDate(2024, 2, 29));

        // Некорректные даты
        assertFalse("2025-02-29 не должна быть валидной (не високосный)",
                DateTimeUtils.isValidDate(2025, 2, 29));
        assertFalse("2025-13-01 не должна быть валидной",
                DateTimeUtils.isValidDate(2025, 13, 1));
        assertFalse("2025-01-32 не должна быть валидной",
                DateTimeUtils.isValidDate(2025, 1, 32));
        assertFalse("2025-04-31 не должна быть валидной",
                DateTimeUtils.isValidDate(2025, 4, 31));

        System.out.println("    Даты валидируются корректно");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEventInvalidTime() {
        System.out.println("    Тест: Добавление события с неверным временем (ожидается исключение)");

        Month month = new Month(1);
        month.addEvent(1, "24:00", "Неверное время", "Описание");
    }

    @Test
    public void testDayEventValidation() {
        System.out.println("    Тест: Валидация событий в дне");

        Day day = new Day(15);

        day.addEvent("00:00", "Полночь", "Событие в полночь");
        day.addEvent("12:00", "Полдень", "Событие в полдень");
        day.addEvent("23:59", "Почти полночь", "Последнее событие дня");

        assertEquals("Должно быть 3 события", 3, day.getEventsCount());
        assertNotNull("Событие в 12:00 должно существовать", day.getEvent("12:00"));

        System.out.println("    События в дне валидируются корректно");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testYearBoundary() {
        System.out.println("    Тест: Границы года (ожидается исключение)");

        User user = new User(1, "Тест");

        Year invalidYear = user.getYear(2126);
    }

    @Test
    public void testMonthNames() {
        System.out.println("   ▶ Тест: Названия месяцев");

        assertEquals("Январь", Month.getMonthName(1));
        assertEquals("Февраль", Month.getMonthName(2));
        assertEquals("Декабрь", Month.getMonthName(12));

        Month january = new Month(1);
        assertEquals("Январь должен быть первым месяцем", 1, january.getMonthNumber());
        assertEquals("В январе 31 день", 31, january.getDaysInMonth());

        Month february = new Month(2);
        assertEquals("В феврале 28 дней (не високосный)", 28, february.getDaysInMonth());

        System.out.println("    Названия и дни месяцев корректны");
    }

    @Test
    public void testDateTimeParsing() {
        System.out.println("    Тест: Парсинг даты и времени");

        try {
            java.time.LocalDateTime dt = DateTimeUtils.parseDateTime("25.12.2025", "14:30");
            assertNotNull(dt);
            assertEquals(2025, dt.getYear());
            assertEquals(12, dt.getMonthValue());
            assertEquals(25, dt.getDayOfMonth());
            assertEquals(14, dt.getHour());
            assertEquals(30, dt.getMinute());

            String formatted = DateTimeUtils.formatDateTime(dt);
            assertEquals("25.12.2025 14:30", formatted);

            System.out.println("    Парсинг и форматирование даты работают");

        } catch (Exception e) {
            fail("Парсинг должен работать: " + e.getMessage());
        }
    }

    @Test
    public void testDateTimeFormatting() {
        System.out.println("   Тест: Форматирование даты и времени");

        java.time.LocalDateTime dt = java.time.LocalDateTime.of(2025, 6, 15, 9, 30);

        assertEquals("15.06.2025", DateTimeUtils.formatDate(dt));
        assertEquals("09:30", DateTimeUtils.formatTime(dt));
        assertEquals("15.06.2025 09:30", DateTimeUtils.formatDateTime(dt));

        System.out.println("    Форматирование даты и времени корректно");
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}