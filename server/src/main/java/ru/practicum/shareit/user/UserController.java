package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserDtoService userDtoService;

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        return userDtoService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        return userDtoService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return userDtoService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userDtoService.findAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userDtoService.delete(userId);
    }
}
