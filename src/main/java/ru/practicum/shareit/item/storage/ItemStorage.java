package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item add(Item item);
    Item update(Item item);
    Item findById(long id);
    List<Item> findAllByUserId(long userId);
    List<Item> search(String text);
}
