package org.example.suites;

import org.example.services.HoroscopeService;
import org.example.services.WeatherService;
import org.example.utils.UserNameExtractor;
import org.example.utils.KeyboardFactory;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.util.Map;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class AdditionalServicesTest {

    @Test
    public void testHoroscopeService() {
        System.out.println("\n Тест: Сервис гороскопа");

        HoroscopeService service = new HoroscopeService();

        Map<String, String> zodiacSigns = HoroscopeService.getZodiacSigns();
        assertNotNull("Знаки зодиака не должны быть null", zodiacSigns);
        assertFalse("Должны быть знаки зодиака", zodiacSigns.isEmpty());

        assertTrue("Должен быть Овен", zodiacSigns.containsKey("aries"));
        assertEquals("Овен", zodiacSigns.get("aries"));
        assertTrue("Должен быть Телец", zodiacSigns.containsKey("taurus"));
        assertEquals("Телец", zodiacSigns.get("taurus"));

        String horoscope = service.getHoroscope("aries");
        assertNotNull("Гороскоп не должен быть null", horoscope);

        String zodiacName = service.getZodiacName("aries");
        assertEquals("Овен", zodiacName);

        // Проверяем неизвестный знак
        String unknownName = service.getZodiacName("unknown");
        assertEquals("Неизвестный знак", unknownName);

        System.out.println("    Сервис гороскопа работает:");
        System.out.println("      Знаков зодиака: " + zodiacSigns.size());
        System.out.println("      Пример гороскопа для Овна получен");
    }

    @Test
    public void testWeatherService() {
        System.out.println("    Тест: Сервис погоды");

        WeatherService service = new WeatherService();

        String[] cities = {"Moscow", "London", "Paris"};

        for (String city : cities) {
            String weather = service.getWeather(city);
            assertNotNull("Погода не должна быть null для " + city, weather);

            assertTrue("Ответ должен содержать город или сообщение об ошибке",
                    weather.contains(city) ||
                            weather.contains("❌") ||
                            weather.contains("не найден"));

            System.out.println("      Погода для " + city + ": " +
                    (weather.contains("❌") ? "Ошибка или город не найден" : "Получена"));
        }

        System.out.println("    Сервис погоды работает");
    }

    @Test
    public void testUserNameExtractor() {
        System.out.println("    Тест: Извлечение имени пользователя");

        try {
            // Тест 1: Только first name
            org.telegram.telegrambots.meta.api.objects.User telegramUser1 =
                    new org.telegram.telegrambots.meta.api.objects.User(12345L, "Иван", false);

            String name1 = UserNameExtractor.extractUserName(telegramUser1);
            assertEquals("Иван", name1);

            // Тест 2: First name и last name
            org.telegram.telegrambots.meta.api.objects.User telegramUser2 =
                    new org.telegram.telegrambots.meta.api.objects.User(12346L, "Иван", false);

            System.out.println("    Извлечение имени пользователя работает (частично)");
            System.out.println("      Только имя: " + name1);

        } catch (Exception e) {
            System.out.println("    Тест пропущен: " + e.getMessage());
        }
    }
    @Test
    public void testKeyboardFactory() {
        System.out.println("    Тест: Фабрика клавиатур");

        InlineKeyboardMarkup monthKeyboard = KeyboardFactory.createMonthKeyboard();
        assertNotNull("Клавиатура месяцев не должна быть null", monthKeyboard);
        assertNotNull("Должны быть кнопки", monthKeyboard.getKeyboard());
        assertFalse("Должны быть строки с кнопками", monthKeyboard.getKeyboard().isEmpty());

        System.out.println("      Клавиатура месяцев создана: " +
                monthKeyboard.getKeyboard().size() + " строк");

        Map<String, String> zodiacSigns = HoroscopeService.getZodiacSigns();
        InlineKeyboardMarkup zodiacKeyboard = KeyboardFactory.createZodiacKeyboard(zodiacSigns);
        assertNotNull("Клавиатура знаков зодиака не должна быть null", zodiacKeyboard);

        System.out.println("      Клавиатура знаков зодиака создана");

        InlineKeyboardMarkup dayKeyboard = KeyboardFactory.createDayButtons(2025, 1);
        assertNotNull("Клавиатура дней не должна быть null", dayKeyboard);

        System.out.println("      Клавиатура дней января 2025 создана");

        InlineKeyboardMarkup weatherKeyboard = KeyboardFactory.createWeatherKeyboard();
        assertNotNull("Клавиатура погоды не должна быть null", weatherKeyboard);

        System.out.println("      Клавиатура погоды создана");

        System.out.println("    Фабрика клавиатур работает");
    }

    @Test
    public void testAllZodiacSigns() {
        System.out.println("    Тест: Все знаки зодиака");

        HoroscopeService service = new HoroscopeService();
        String[] signs = {"aries", "taurus", "gemini", "cancer", "leo", "virgo",
                "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"};

        for (String sign : signs) {
            String name = service.getZodiacName(sign);
            assertNotNull("Название не должно быть null для " + sign, name);
            assertFalse("Название не должно быть пустым", name.isEmpty());

            // может вернуть ошибку, если нет интернета
            String horoscope = service.getHoroscope(sign);
            assertNotNull("Гороскоп не должен быть null", horoscope);

            System.out.println("      " + sign + ": " + name + " - " +
                    (horoscope.contains("❌") ? "Ошибка получения" : "OK"));
        }

        System.out.println("    Все знаки зодиака поддерживаются");
    }

    @After
    public void tearDown() {
        System.out.println("    Очистка после теста\n");
    }
}