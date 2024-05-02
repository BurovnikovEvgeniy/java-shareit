package ru.practicum.shareit.user.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDtoServiceImpl implements UserDtoService {

    private final UserStorage userStorage;

    @Autowired
    public UserDtoServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto add(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.error("Заданное name пользователя для добавления данных не валидно (name=" + userDto.getName() + ")");
            throw new NotValidDataException("Заданное name пользователя для добавления данных не валидно (name=\" + userDto.getName() + \")");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.error("Заданный email пользователя для добавления данных не валиден (email=" + userDto.getEmail() + ")");
            throw new NotValidDataException("Заданный email пользователя для добавления данных не валиден (email=" + userDto.getEmail() + ")");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.add(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (id == null || id < 0) {
            log.error("Заданное id пользователя для обновления данных не валидно (id=" + id + ")");
            throw new NotValidDataException("Заданное id пользователя для обновления данных не валидно (id=" + id + ")");
        }
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
        }
        else {
            return UserMapper.toUserDto(userStorage.update(id, UserMapper.toUser(userDto)));
        }
    }

    @Override
    public UserDto findById(Long id) {
        if (id == null || id < 0) {
            log.error("Заданное id пользователя для поиска по id не валидно (id=" + id + ")");
            throw new NotValidDataException("Заданное id пользователя для поиска по id не валидно (id=" + id + ")");
        }
        return UserMapper.toUserDto(userStorage.findById(id));
    }

    @Override
    public void delete(Long id) {
        if (id == null || id < 0) {
            log.error("Заданное id для удаления пользователя не валидно (id=" + id + ")");
            throw new NotValidDataException("Заданное id для удаления пользователя не валидно (id=" + id + ")");
        }
        userStorage.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
