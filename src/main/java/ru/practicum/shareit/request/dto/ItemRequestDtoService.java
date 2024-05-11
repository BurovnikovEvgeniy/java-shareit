package ru.practicum.shareit.request.dto;

import java.util.List;

public interface ItemRequestDtoService {

    ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOut> getUserRequests(Long userId);

    List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoOut getRequestById(Long userId, Long requestId);
}
