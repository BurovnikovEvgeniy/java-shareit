package ru.practicum.shareit.exception.entity;

public class NotValidDataException extends RuntimeException {

    public NotValidDataException(String message) {
        super(message);
    }
}
