package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_HEADER) Long userId, @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("POST запрос на добавление пользователя с id - " + userId + " предмета " + itemDto.toString());
        return itemService.createItem(userId, itemDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_HEADER) Long userId, @Validated(Update.class) @RequestBody ItemDto itemDto, @PathVariable @Positive @NotNull Long itemId) {
        log.info("PATCH запрос на обновление предмета с id - " + itemId + " у пользователя с id - " + userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public Collection<ItemDtoInfo> findAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("GET запрос на получение вещей пользователя с id - " + userId);
        return itemService.getAllItemUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        log.info("GET запрос на поиск предметов");
        return itemService.searchItem(text);
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo findById(@RequestHeader(USER_HEADER) Long userId, @PathVariable @Positive @NotNull Long itemId) {
        log.info("GET запрос на получение предмета с id - " + itemId + " пользователя с id - " + userId);
        return itemService.findItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(USER_HEADER) Long userId, @PathVariable @Positive @NotNull Long itemId) {
        log.warn("POST запрос на создание комментария");
        return itemService.createComment(commentDto, userId, itemId);
    }
}
