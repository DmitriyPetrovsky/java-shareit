package ru.practicum.shareit.server.exception;

public class DoubleEmailException extends RuntimeException {
    public DoubleEmailException(String message) {
        super(message);
    }
}
