package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoInfo getItemById(@PathVariable Long itemId,
                                   @RequestHeader(HEADER_USER) Long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoInfo> getAllItemUser(@RequestHeader(HEADER_USER) Long userId,
                                                  @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                  @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return itemService.getAllItemUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(HEADER_USER) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(HEADER_USER) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestHeader(HEADER_USER) Long userId,
                                           @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                           @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return itemService.searchItems(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(HEADER_USER) Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(commentDto, userId, itemId);
    }
}
