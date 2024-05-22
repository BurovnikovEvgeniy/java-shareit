package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.exception.entity.UnsupportedStatusException;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFoundException(final EntityNotFoundException e) {
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidDataException(final NotValidDataException e) {
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.error("Произошла ошибка валидации параметров");
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Ошибка валидации параметра функции ");
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupportedStatusException(final UnsupportedStatusException e) {
        log.error("Ошибка валидации параметра функции ");
        return Map.of("error", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherException(final Throwable e) {
        log.error("INTERNAL_SERVER_ERROR, обработчик Throwable gateway");
        return Map.of("error", e.getMessage());
    }
}
