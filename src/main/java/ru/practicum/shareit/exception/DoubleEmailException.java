package ru.practicum.shareit.exception;

public class DoubleEmailException extends RuntimeException {
    public DoubleEmailException(String message) {
        super(message);
    }
}
