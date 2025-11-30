package org.example;

import org.example.services.UserData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {

    private static final String DB_URL = "jdbc:sqlite:telegram_bot.db";

    public DataBaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement()) {

            // Table for users
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY, " +
                    "username TEXT, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

            // Table for reminders
            String createRemindersTable = "CREATE TABLE IF NOT EXISTS reminders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "year INTEGER, " +
                    "month TEXT, " +
                    "day INTEGER, " +
                    "reminder_time TEXT NOT NULL, " +
                    "reminder_name TEXT, " +
                    "description TEXT, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users (user_id))";

            statement.execute(createUsersTable);
            statement.execute(createRemindersTable);

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    public void saveUser(Long userId) {
        String sql = "INSERT OR REPLACE INTO users (user_id) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setLong(1, userId);
            pstatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to save user: " + e.getMessage());
        }
    }

    public void saveReminder(Long userId, UserData userData) {
        String sql = "INSERT INTO reminders (user_id, year, month, day, reminder_time, reminder_name, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setLong(1, userId);
            pstatement.setInt(2, userData.getYear());
            pstatement.setString(3, userData.getMonth());
            pstatement.setInt(4, userData.getDay());
            pstatement.setString(5, userData.getReminderTime()); // Changed to String
            pstatement.setString(6, userData.getReminderName());
            pstatement.setString(7, userData.getDescription()); // Fixed method call
            pstatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to save reminder: " + e.getMessage());
        }
    }

    public List<UserData> getLastReminders(Long userId, int limit) {
        List<UserData> reminders = new ArrayList<>();
        String sql = "SELECT year, month, day, reminder_time, reminder_name, description, created_at " +
                "FROM reminders " +
                "WHERE user_id = ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setInt(2, limit);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                UserData reminder = new UserData();
                reminder.setYear(rs.getInt("year"));
                reminder.setMonth(rs.getString("month"));
                reminder.setDay(rs.getInt("day"));
                reminder.setReminderTime(rs.getString("reminder_time")); // Changed to String
                reminder.setReminderName(rs.getString("reminder_name"));
                reminder.setDescription(rs.getString("description"));
                reminders.add(reminder);
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve reminders: " + e.getMessage());
        }
        return reminders;
    }

    public int getUserReminderCount(Long userId) {
        String sql = "SELECT COUNT(*) as count FROM reminders WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting reminder count: " + e.getMessage());
        }

        return 0; // Return 0 in case of error
    }
}