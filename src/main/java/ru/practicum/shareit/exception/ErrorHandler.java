package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotUniqueEmailException;
import ru.practicum.shareit.exception.entity.NotValidDataException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFoundException(final EntityNotFoundException e) {
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNotUniqueEmailException(final NotUniqueEmailException e) {
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidEmailException(final NotValidDataException e) {
        return Map.of("message", e.getMessage());
    }
}
