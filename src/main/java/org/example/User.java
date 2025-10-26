package org.example;

public class User {
    private int id;
    private String name;
    private Year[] years;
    private static final int START_YEAR = 2025;
    private static final int TOTAL_YEARS = 100;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.years = new Year[TOTAL_YEARS];
    }

    // возвращает год если он уже создан иначе ноль
    public Year getExistingYear(int yearNumber) {
        int index = yearNumber - START_YEAR;
        if (index < 0 || index >= TOTAL_YEARS) return null;
        return years[index];
    }

    public Year getYear(int yearNumber) {
        int index = yearNumber - START_YEAR;

        if (index < 0 || index >= TOTAL_YEARS) {
            throw new IllegalArgumentException("Неверный год: " + yearNumber);
        }

        if (years[index] == null) {
            years[index] = new Year(yearNumber);
        }
        return years[index];
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTotalEvents() {
        int total = 0;
        for (Year year : years) {
            if (year != null) {
                total += year.getTotalEvents();
            }
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
