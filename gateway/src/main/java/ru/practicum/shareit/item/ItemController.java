package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
    private final ItemClient itemClient;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader(USER_HEADER) Long userId,
                                             @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("POST запрос на добавление пользователя с id - " + userId + " предмета " + itemDto.toString());
        return itemClient.createItem(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_HEADER) Long userId,
                                             @Validated(Update.class) @RequestBody ItemDto itemDto,
                                             @PathVariable @Positive @NotNull Long itemId) {
        log.info("PATCH запрос на обновление предмета с id - " + itemId + " у пользователя с id - " + userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_HEADER) Long userId,
                                          @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @Min(0) Integer from,
                                          @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(1) Integer size) {
        log.info("GET запрос на получение вещей пользователя с id - " + userId);
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader(USER_HEADER) Long userId,
                                             @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @Min(0) Integer from,
                                             @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(1) Integer size) {
        log.info("GET запрос на поиск предметов");
        return itemClient.searchItem(text, userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_HEADER) Long userId,
                                           @PathVariable @Positive @NotNull Long itemId) {
        log.info("GET запрос на получение предмета с id - " + itemId + " пользователя с id - " + userId);
        return itemClient.findById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(USER_HEADER) Long userId,
                                                @PathVariable @Positive @NotNull Long itemId) {
        log.warn("POST запрос на создание комментария");
        return itemClient.createComment(commentDto, userId, itemId);
    }
}
