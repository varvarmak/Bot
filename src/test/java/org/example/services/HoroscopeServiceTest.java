package org.example.services;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class HoroscopeServiceTest {

    @Test
    public void testGetZodiacSigns() {
        Map<String, String> zodiacSigns = HoroscopeService.getZodiacSigns();
        assertNotNull(zodiacSigns);
        assertEquals(12, zodiacSigns.size());
        assertEquals("Овен", zodiacSigns.get("aries"));
    }

    @Test
    public void testGetHoroscopeMethodExists() {
        HoroscopeService service = new HoroscopeService();
        String result = service.getHoroscope("aries");
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
    }
    @Test
    public void testGetZodiacNameForAllSigns() {
        HoroscopeService service = new HoroscopeService();
        Map<String, String> signs = HoroscopeService.getZodiacSigns();

        for (String key : signs.keySet()) {
            assertEquals(signs.get(key), service.getZodiacName(key));
        }
    }

    @Test
    public void testGetHoroscopeWithInvalidSign() {
        HoroscopeService service = new HoroscopeService();
        String result = service.getHoroscope("invalid_sign");
        assertNotNull(result);
        assertTrue(result.contains("❌") || result.contains("Не удалось"));
    }
}