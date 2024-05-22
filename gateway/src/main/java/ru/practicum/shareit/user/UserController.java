package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.CreateUserValidation;
import ru.practicum.shareit.utils.UpdateUserValidation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@Validated(value = {CreateUserValidation.class}) @RequestBody UserDto user) {
        log.info("POST запрос на создание пользователя: {}", user);
        return userClient.add(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PositiveOrZero @PathVariable Long userId,
                                         @Validated(value = {UpdateUserValidation.class}) @NotNull @RequestBody UserDto userDto) {
        log.info("PATCH запрос на обновление пользователя c id: {}", userId);
        return userClient.update(userId, userDto);
    }


    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET запрос на получение списка всех пользователей.");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        log.info("GET запрос на получение пользователя c id: {}", userId);
        return userClient.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("DELETE запрос на удаление пользователя с id: {}", userId);
        return userClient.deleteById(userId);
    }
}
