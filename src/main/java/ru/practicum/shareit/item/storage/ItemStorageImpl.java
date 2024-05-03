package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {

    private static final Map<Long, List<Item>> itemMap = new HashMap<>();
    private static Long index = 0L;

    @Override
    public Item add(Item item) {
        item.setId(++index);
        if (!itemMap.containsKey(item.getOwner())) {
            itemMap.put(item.getOwner(), new ArrayList<>());
        }
        itemMap.get(item.getOwner()).add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!itemMap.containsKey(item.getOwner())) {
            log.error("У пользователя с id=" + item.getOwner() + " не найдено вещей в базе, обновление данных его вещей невозможно");
            throw new EntityNotFoundException("У пользователя с id=" + item.getOwner() + " не найдено вещей в базе, обновление данных его вещей невозможно");
        }
        List<Item> userItems = itemMap.get(item.getOwner());
        Item oldItem = userItems.stream()
                .filter(el -> el.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Вещь с id=" + item.getId() + " не найдена в базе");
                    return new EntityNotFoundException("Вещь с id=" + item.getId() + " не найдена в базе");
                });
        userItems.remove(oldItem);
        userItems.add(item);
        return item;
    }

    @Override
    public Item findById(long id) {
        return itemMap.values()
                .stream().flatMap(Collection::stream)
                .filter(item -> item.getId().equals(id))
                .findFirst().orElseThrow(() -> {
                    log.error("Вещь с id=" + id + " не найдена в базе");
                    throw new EntityNotFoundException("Вещь с id=" + id + " не найдена в базе");
                });
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return new ArrayList<>(Optional.ofNullable(itemMap.get(userId)).orElseThrow(() -> {
            log.error("Вещи пользователя с id=" + userId + " не найдены в базе");
            throw new EntityNotFoundException("Вещи пользователя с id=" + userId + " не найдены в базе");
        }));
    }

    @Override
    public List<Item> search(String text) {
        return itemMap.values().stream()
                .flatMap(Collection::stream)
                .filter(el -> el.getAvailable().equals(true))
                .filter(el -> el.getName().toLowerCase().contains(text.toLowerCase()) || el.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
