package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotUniqueEmailException;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.user.model.User;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> userMap = new HashMap<>();
    private final Set<String> emailSet = new HashSet<>();
    private static Long index = 0L;

    @Override
    public User add(User user) {
        checkEmail(user);
        user.setId(++index);
        userMap.put(user.getId(), user);
        emailSet.add(user.getEmail());
        return userMap.get(user.getId());
    }

    @Override
    public User update(Long id, User user) {
        if (!userMap.containsKey(id)) {
            log.error("Пользователь с id=" + id + " не найден");
            throw new EntityNotFoundException("Пользователь с id=" + id + " не найден");
        }
        User oldUser = userMap.get(id);
        if (!oldUser.getEmail().equals(user.getEmail())) {
            checkEmail(user);
            emailSet.remove(userMap.get(id).getEmail());
            emailSet.add(user.getEmail());
        }
        user.setId(id);
        userMap.put(id, user);
        return userMap.get(user.getId());
    }

    @Override
    public User findById(Long id) {
        return Optional.ofNullable(userMap.get(id)).orElseThrow(() -> {
            log.error("Пользователь с id=" + id + " не найден");
            throw new EntityNotFoundException("Пользователь с id=" + id + " не найден");
        });
    }

    @Override
    public void delete(Long id) {
        if (!userMap.containsKey(id)) {
            log.error("Пользователь с id=" + id + " не найден");
            throw new EntityNotFoundException("Пользователь с id=" + id + " не найден");
        }
        emailSet.remove(userMap.get(id).getEmail());
        userMap.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    private void checkEmail(User user) {

        if (emailSet.contains(user.getEmail())) {
            log.error("Email:" + user.getEmail() + " пользователя c id=" + user.getId() + " уже существует");
            throw new NotUniqueEmailException("Email:" + user.getEmail() + " пользователя c id=" + user.getId() + " уже существует");
        }

        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            log.error("Email:" + user.getEmail() + " пользователя c id=" + user.getId() + " не валиден");
            throw new NotValidDataException("Email:" + user.getEmail() + " пользователя c id=" + user.getId() + " не валиден");
        }
    }
}
