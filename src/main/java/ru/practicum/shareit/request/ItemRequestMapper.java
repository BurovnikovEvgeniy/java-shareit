package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.itemMapper;

@Mapper
public interface ItemRequestMapper {

    ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequest toRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toRequestDto(ItemRequest request);

    default ItemRequestDtoOut toRequestDtoOut(ItemRequest request) {
        List<ItemDtoOut> itemsDtoOut = new ArrayList<>();

        if (Objects.nonNull(request.getItems())) {
            itemsDtoOut = request.getItems().stream()
                    .map(itemMapper::toItemDtoOut)
                    .collect(Collectors.toList());
        }
        return new ItemRequestDtoOut(request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemsDtoOut);
    }
}
