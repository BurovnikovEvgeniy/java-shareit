package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDtoServiceImpl implements UserDtoService {

    private final UserStorage userStorage;

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.add(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            log.warn("Происходит частичное обновление данных пользователя с id=" + id);
            UserDto oldUser = findById(id);
            UserDto userWithOldData = new UserDto(id, userDto.getName(), userDto.getEmail());
            if (userDto.getName() == null) {
                userWithOldData.setName(oldUser.getName());
            }
            if (userDto.getEmail() == null) {
                userWithOldData.setEmail(oldUser.getEmail());
            }
            return UserMapper.toUserDto(userStorage.update(id, UserMapper.toUser(userWithOldData)));
        } else {
            return UserMapper.toUserDto(userStorage.update(id, UserMapper.toUser(userDto)));
        }
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.toUserDto(userStorage.findById(id));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
