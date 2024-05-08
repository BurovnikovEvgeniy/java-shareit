package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.validated.group.CreateUserValidation;
import ru.practicum.shareit.user.validated.group.UpdateUserValidation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserDtoService userDtoService;

    @PostMapping
    public UserDto add(@Validated(value = {CreateUserValidation.class}) @NotNull @RequestBody UserDto userDto) {
        return userDtoService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PositiveOrZero @PathVariable Long userId,
                          @Validated(value = {UpdateUserValidation.class}) @NotNull @RequestBody UserDto userDto) {
        return userDtoService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PositiveOrZero @PathVariable Long userId) {
        return userDtoService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userDtoService.findAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PositiveOrZero @PathVariable Long userId) {
        userDtoService.delete(userId);
    }
}
