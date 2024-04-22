package ru.practicum.shareit.exection;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}