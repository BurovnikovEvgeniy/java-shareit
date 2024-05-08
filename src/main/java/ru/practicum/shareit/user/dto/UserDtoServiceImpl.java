package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDtoServiceImpl implements UserDtoService {

    private final UserRepository userStorage;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с " + id + " не существует"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return UserMapper.toUserDto(userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с " + id + " не существует"))
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userStorage.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}