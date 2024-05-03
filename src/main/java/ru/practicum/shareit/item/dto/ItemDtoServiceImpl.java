package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemDtoServiceImpl implements ItemDtoService {

    private final ItemStorage itemStorage;
    private final UserDtoService userDtoService;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        UserDto user = userDtoService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner((UserMapper.toUser(user)).getId());
        return ItemMapper.toItemDto(itemStorage.add(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        UserDto ownerDto = userDtoService.findById(userId);
        Item resItem;
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            log.warn("Происходит частичное обновление данных о вещи с id=" + itemId);
            ItemDto oldItem = findById(ownerDto.getId(), itemId);
            ItemDto itemWithOldData = new ItemDto(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequest());
            if (itemDto.getName() == null) {
                itemWithOldData.setName(oldItem.getName());
            }
            if (itemDto.getDescription() == null) {
                itemWithOldData.setDescription(oldItem.getDescription());
            }
            if (itemDto.getAvailable() == null) {
                itemWithOldData.setAvailable(oldItem.getAvailable());
            }
            resItem = ItemMapper.toItem(itemWithOldData);
        } else {
            resItem = ItemMapper.toItem(itemDto);
        }
        resItem.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.update(resItem));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        userDtoService.findById(userId);
        Item item = itemStorage.findById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        UserDto user = userDtoService.findById(userId);
        return itemStorage.findAllByUserId(user.getId())
                .stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        userDtoService.findById(userId);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.search(text)
                .stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
