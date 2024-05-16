package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.itemRequestMapper;
import static ru.practicum.shareit.user.UserMapper.userMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestDtoServiceImpl implements ItemRequestDtoService {

    private final UserDtoService userService;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto) {
        User user = userMapper.toUser(userService.findById(userId));
        ItemRequest request = itemRequestMapper.toRequest(itemRequestDto);
        request.setRequester(user);
        return itemRequestMapper.toRequestDtoOut(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDtoOut> getUserRequests(Long userId) {
        if (!userService.isUserExist(userId)) {
            throw new EntityNotFoundException("Пользователя с id=" + userId + " не существует");
        }
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterId(userId);
        return itemRequestList.stream()
                .map(itemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getAllRequests(Long userId, Pageable pageable) {
        if (!userService.isUserExist(userId)) {
            throw new EntityNotFoundException("Пользователя с id=" + userId + " не существует");
        }
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(userId, pageable);
        return itemRequestList.stream()
                .map(itemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOut getRequestById(Long userId, Long requestId) {
        if (!userService.isUserExist(userId)) {
            throw new EntityNotFoundException("Пользователя с id=" + userId + " не существует");
        }
        Optional<ItemRequest> requestById = requestRepository.findById(requestId);
        if (requestById.isEmpty()) {
            throw new EntityNotFoundException("Запрос с id=" + requestId + " не был найден");
        }
        return itemRequestMapper.toRequestDtoOut(requestById.get());
    }
}
