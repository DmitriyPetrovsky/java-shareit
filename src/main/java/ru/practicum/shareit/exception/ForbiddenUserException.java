package ru.practicum.shareit.exception;

public class ForbiddenUserException extends RuntimeException {
    public ForbiddenUserException(String message) {
        super(message);
    }
}
