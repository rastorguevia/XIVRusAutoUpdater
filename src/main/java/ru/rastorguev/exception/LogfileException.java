package ru.rastorguev.exception;

public class LogfileException extends RuntimeException {
    public LogfileException(String message, Throwable cause) {
        super(message, cause);
    }
}
