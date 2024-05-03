package ru.practicum.shareit.user.dto;

import java.util.List;

public interface UserDtoService {

    UserDto add(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    UserDto findById(Long id);

    void delete(Long id);

    List<UserDto> findAll();
}
