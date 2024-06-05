package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.Constant.PAGE_FROM_DEFAULT;
import static ru.practicum.shareit.Constant.PAGE_SIZE_DEFAULT;

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
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        log.info("POST запрос на добавление пользователя с id - " + userId + " предмета " + itemDto.toString());
        return itemService.createItem(userId, itemDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_HEADER) Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("PATCH запрос на обновление предмета с id - " + itemId + " у пользователя с id - " + userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public Collection<ItemDtoInfo> findAll(@RequestHeader(USER_HEADER) Long userId,
                                           @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                           @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        log.info("GET запрос на получение вещей пользователя с id - " + userId);
        return itemService.getAllItemUser(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text,
                                          @RequestHeader(USER_HEADER) Long userId,
                                          @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                          @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        log.info("GET запрос на поиск предметов");
        return itemService.searchItem(text, userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo findById(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("GET запрос на получение предмета с id - " + itemId + " пользователя с id - " + userId);
        return itemService.findItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_HEADER) Long userId,
                                    @PathVariable Long itemId) {
        log.warn("POST запрос на создание комментария");
        return itemService.createComment(commentDto, userId, itemId);
    }
}
