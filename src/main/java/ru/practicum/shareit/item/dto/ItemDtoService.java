package ru.practicum.shareit.item.dto;

import java.util.List;

public interface ItemDtoService {

    ItemDtoOut add(Long userId, ItemDto itemDto);

    ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoOut findById(Long userId, Long itemId);

    List<ItemDtoOut> findAllByUserId(Long userId);

    List<ItemDtoOut> search(Long userId, String text);

    CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId);
}
