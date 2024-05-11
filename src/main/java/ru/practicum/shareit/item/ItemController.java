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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemDtoService itemDtoService;

    @PostMapping
    public ItemDtoOut add(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                          @Valid @NotNull @RequestBody ItemDto itemDto) {
        return itemDtoService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                          @NotNull @RequestBody ItemDto itemDto,
                          @PositiveOrZero @PathVariable Long itemId) {
        return itemDtoService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut findById(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                            @PositiveOrZero @PathVariable("itemId") Long itemId) {
        return itemDtoService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOut> findAllByUserId(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                                            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemDtoService.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> search(@PositiveOrZero @RequestHeader(USER_HEADER) Long userId,
                                   @RequestParam(name = "text") String text,
                                   @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemDtoService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader(USER_HEADER) Long userId,
                                       @Validated @RequestBody CommentDto commentDto,
                                       @PathVariable Long itemId) {
        return itemDtoService.createComment(userId, commentDto, itemId);
    }
}
