package ru.practicum.shareit.server.exception;

public class ForbiddenExcepton extends RuntimeException {
    public ForbiddenExcepton(String message) {
        super(message);
    }
}

