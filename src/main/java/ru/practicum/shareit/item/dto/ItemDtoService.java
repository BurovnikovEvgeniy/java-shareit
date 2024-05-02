package ru.practicum.shareit.item.dto;

import java.util.List;

public interface ItemDtoService {
    ItemDto add(Long userId, ItemDto itemDto);
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);
    ItemDto findById(Long userId, Long itemId);
    List<ItemDto> findAllByUserId(Long userId);
    List<ItemDto> search(Long userId, String text);
}
