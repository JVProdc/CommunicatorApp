package cz.test.test1;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {
    // Method to get the current day of the week (0 for Sunday, 1 for Monday, ..., 6 for Saturday)
    public static int getCurrentDayOfWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek.getValue() % 7; // Adjust to start from 0 (Sunday)
    }
}