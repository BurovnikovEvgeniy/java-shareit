package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemDtoService itemDtoService;

    @Autowired
    public ItemController(ItemDtoService itemDtoService) {
        this.itemDtoService = itemDtoService;
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        return itemDtoService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemDtoService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_HEADER) Long userId, @PathVariable("itemId") Long itemId) {
        return itemDtoService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader(USER_HEADER) Long userId) {
        return itemDtoService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_HEADER) Long userId, @RequestParam(name = "text") String text) {
        return itemDtoService.search(userId, text);
    }
}
