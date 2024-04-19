package ru.rastorguev.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static LocalDateTime getTranslationFileExecuteDateTime(String filename) {
        return LocalDateTime.parse(filename.substring(filename.length() - 19), DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
    }

    public static Long getTimeConsumption(long timer) {
        return (System.currentTimeMillis() - timer);
    }

}
