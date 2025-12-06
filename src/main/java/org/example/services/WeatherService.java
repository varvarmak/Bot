package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.net.URI;

public class WeatherService {
    private final String apiKey = "467ed7f7b4cb09f561501859d15d5db0";
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
    private final ObjectMapper mapper = new ObjectMapper();

    public String getWeather(String city) {
        if (apiKey == null || apiKey.contains("ТВОЙ_API")) {
            return "⚠️ API ключ не настроен";
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ru",
                    baseUrl, city, apiKey);

            var request = new HttpGet(URI.create(url));
            var response = client.execute(request);

            if (response.getCode() == 200) {
                JsonNode jsonNode = mapper.readTree(response.getEntity().getContent());
                double temp = jsonNode.get("main").get("temp").asDouble();
                String description = jsonNode.get("weather").get(0).get("description").asText();
                return String.format("🌤️ %s: %.1f°C\n%s", city, temp, description);
            } else {
                return "❌ Город не найден";
            }
        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }
}
