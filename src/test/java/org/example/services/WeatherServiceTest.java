package org.example.services;

import org.junit.Test;
import static org.junit.Assert.*;

public class WeatherServiceTest {

    @Test
    public void testWeatherServiceCreation() {
        WeatherService service = new WeatherService();
        assertNotNull(service);
    }
    @Test
    public void testGetWeatherWithInvalidCity() {
        WeatherService service = new WeatherService();
        String result = service.getWeather("InvalidCity123");
        assertNotNull(result);
        assertTrue(result.contains("❌") || result.contains("не найден"));
    }

    @Test
    public void testGetWeatherWithNull() {
        WeatherService service = new WeatherService();
        String result = service.getWeather(null);
        assertNotNull(result);
        assertTrue(result.contains("❌") || result.contains("Ошибка"));
    }

    @Test
    public void testGetWeatherMethodExists() {
        WeatherService service = new WeatherService();
        String result = service.getWeather("Moscow");
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
    }
}