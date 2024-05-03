package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemDtoService itemDtoService;

    @PostMapping
    public ItemDto add(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                       @Valid @NotNull @RequestBody ItemDto itemDto) {
        return itemDtoService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                          @NotNull @RequestBody ItemDto itemDto,
                          @PositiveOrZero @PathVariable Long itemId) {
        return itemDtoService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                            @PositiveOrZero @PathVariable("itemId") Long itemId) {
        return itemDtoService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId) {
        return itemDtoService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                                @RequestParam(name = "text") String text) {
        return itemDtoService.search(userId, text);
    }
}
