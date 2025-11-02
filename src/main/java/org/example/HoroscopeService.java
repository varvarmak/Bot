package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HoroscopeService {
    private static final Map<String, String> ZODIAC_SIGNS = new HashMap<>();

    static {
        // Инициализируем знаки зодиака
        ZODIAC_SIGNS.put("aries", "Овен");
        ZODIAC_SIGNS.put("taurus", "Телец");
        ZODIAC_SIGNS.put("gemini", "Близнецы");
        ZODIAC_SIGNS.put("cancer", "Рак");
        ZODIAC_SIGNS.put("leo", "Лев");
        ZODIAC_SIGNS.put("virgo", "Дева");
        ZODIAC_SIGNS.put("libra", "Весы");
        ZODIAC_SIGNS.put("scorpio", "Скорпион");
        ZODIAC_SIGNS.put("sagittarius", "Стрелец");
        ZODIAC_SIGNS.put("capricorn", "Козерог");
        ZODIAC_SIGNS.put("aquarius", "Водолей");
        ZODIAC_SIGNS.put("pisces", "Рыбы");
    }

    public static Map<String, String> getZodiacSigns() {
        return ZODIAC_SIGNS;
    }

    public String getHoroscope(String zodiacSign) {
        // Пробуем разные источники по очереди
        String result = tryHoroscopesTech(zodiacSign);
        if (isValidHoroscope(result)) {
            return result;
        }

        result = tryHoroMail(zodiacSign);
        if (isValidHoroscope(result)) {
            return result;
        }

        result = tryRambler(zodiacSign);
        if (isValidHoroscope(result)) {
            return result;
        }

        return "❌ Не удалось получить гороскоп. Попробуйте позже.";
    }

    private boolean isValidHoroscope(String horoscope) {
        return horoscope != null &&
                !horoscope.contains("Не удалось") &&
                !horoscope.contains("❌") &&
                horoscope.length() > 50;
    }

    private String tryHoroscopesTech(String zodiacSign) {
        try {
            String url = "https://horoscopes.rambler.ru/" + zodiacSign + "/";
            String html = fetchHtml(url);
            return parseRambler(html);
        } catch (Exception e) {
            return "Не удалось получить гороскоп с Rambler";
        }
    }

    private String tryHoroMail(String zodiacSign) {
        try {
            String url = "https://horo.mail.ru/prediction/" + zodiacSign + "/today/";
            String html = fetchHtml(url);
            return parseHoroMail(html);
        } catch (Exception e) {
            return "Не удалось получить гороскоп с Mail.ru";
        }
    }

    private String tryRambler(String zodiacSign) {
        try {
            String url = "https://horoscopes.rambler.ru/" + zodiacSign + "/";
            String html = fetchHtml(url);
            return parseRamblerNew(html);
        } catch (Exception e) {
            return "Не удалось получить гороскоп с Rambler";
        }
    }

    private String parseHoroMail(String html) {
        try {
            // Новые паттерны для horo.mail.ru
            String[] patterns = {
                    "article__item[^>]*>([^<]+)</div>",
                    "article__text[^>]*>([^<]+)</div>",
                    "prediction__text[^>]*>([^<]+)</div>",
                    "content__text[^>]*>([^<]+)</div>",
                    "<p>([^<]{50,300})</p>"
            };

            for (String pattern : patterns) {
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(html);
                if (m.find()) {
                    String text = cleanText(m.group(1));
                    if (text.length() > 50) {
                        return text;
                    }
                }
            }

            return "Гороскоп временно недоступен на этом источнике.";
        } catch (Exception e) {
            return "Ошибка парсинга: " + e.getMessage();
        }
    }

    private String parseRambler(String html) {
        try {
            // Паттерны для Rambler
            String[] patterns = {
                    "hq__text[^>]*>([^<]+)</div>",
                    "hq__content[^>]*>([^<]{100,500})</div>",
                    "xRU:[^>]*>([^<]{50,300})</div>",
                    "_1BJre[^>]*>([^<]{50,300})</div>"
            };

            for (String pattern : patterns) {
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(html);
                if (m.find()) {
                    String text = cleanText(m.group(1));
                    if (text.length() > 50) {
                        return text;
                    }
                }
            }

            return "Гороскоп временно недоступен на этом источнике.";
        } catch (Exception e) {
            return "Ошибка парсинга: " + e.getMessage();
        }
    }

    private String parseRamblerNew(String html) {
        try {
            // Альтернативный парсинг для Rambler
            Pattern p = Pattern.compile("content\":\"([^\"]{100,500})\"");
            Matcher m = p.matcher(html);
            if (m.find()) {
                String text = cleanText(m.group(1));
                if (text.length() > 50) {
                    return text;
                }
            }

            // Ищем в JSON структуре
            p = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");
            m = p.matcher(html);
            if (m.find()) {
                String text = cleanText(m.group(1));
                if (text.length() > 50) {
                    return text;
                }
            }

            return "Гороскоп временно недоступен на этом источнике.";
        } catch (Exception e) {
            return "Ошибка парсинга: " + e.getMessage();
        }
    }

    private String cleanText(String text) {
        if (text == null) return "";

        return text.replaceAll("&nbsp;", " ")
                .replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String fetchHtml(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        return content.toString();
    }

    public String getZodiacName(String key) {
        return ZODIAC_SIGNS.getOrDefault(key, "Неизвестный знак");
    }
}