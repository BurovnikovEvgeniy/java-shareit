package ru.practicum.shareit.request.dto;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRequestDtoService {

    ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOut> getUserRequests(Long userId);

    List<ItemRequestDtoOut> getAllRequests(Long userId, Pageable pageable);

    ItemRequestDtoOut getRequestById(Long userId, Long requestId);
}
