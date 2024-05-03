package ru.practicum.shareit.exception.entity;

public class NotUniqueEmailException extends RuntimeException {

    public NotUniqueEmailException(String message) {
        super(message);
    }
}
