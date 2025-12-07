package org.example.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyboardFactory {

    public static InlineKeyboardMarkup createMonthKeyboard() {
        String[] months = {
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < months.length; i += 3) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + 3 && j < months.length; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(months[j]);
                button.setCallbackData(months[j]);
                row.add(button);
            }
            rows.add(row);
        }

        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup createZodiacKeyboard(Map<String, String> zodiacSigns) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        int count = 0;

        for (Map.Entry<String, String> entry : zodiacSigns.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getValue());
            button.setCallbackData("horoscope_" + entry.getKey());

            currentRow.add(button);
            count++;

            if (count % 3 == 0) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup createDayButtons(int year, int month) {
        int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 1; i <= daysInMonth; i += 7) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + 7 && j <= daysInMonth; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf(j));
                button.setCallbackData(String.valueOf(j));
                row.add(button);
            }
            rows.add(row);
        }

        markup.setKeyboard(rows);
        return markup;
    }
    public static InlineKeyboardMarkup createWeatherKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText("🌆 Екатеринбург");
        btn1.setCallbackData("weather_ekaterinburg");

        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn2.setText("🏙️ Москва");
        btn2.setCallbackData("weather_moscow");

        row1.add(btn1);
        row1.add(btn2);
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton btn3 = new InlineKeyboardButton();
        btn3.setText("🇰🇿 Костанай");
        btn3.setCallbackData("weather_kostanay");

        row2.add(btn3);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

}