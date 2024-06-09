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

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_HEADER) Long userId) {
        log.info("POST: user request with id={} to create a item, request body={}", userId, itemDto);
        return itemClient.createItem(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                                             @PathVariable @Positive @NotNull Long itemId,
                                             @RequestHeader(USER_HEADER) Long userId) {
        log.info("PATCH: user request with id={} to update a item, request body={}", userId, itemDto);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_HEADER) Long userId,
                                          @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @Min(0) Integer from,
                                          @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(1) Integer size) {
        log.info("GET: user request with id={} to view a items. Page from={}, page size={}", userId, from, size);
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader(USER_HEADER) Long userId,
                                             @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @Min(0) Integer from,
                                             @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(1) Integer size) {
        log.info("GET: user request with id={} to search a item with name or description={}", userId, text);
        return itemClient.searchItem(text, userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable @Positive @NotNull Long itemId,
                                           @RequestHeader(USER_HEADER) Long userId) {
        log.info("GET: user request with id={} to view a item with id={}", userId, itemId);
        return itemClient.findById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(USER_HEADER) Long userId,
                                                @PathVariable @Positive @NotNull Long itemId) {
        log.info("POST: user request with id={} to create a comment on an item with id={}, request body={}",
                userId, itemId, commentDto);
        return itemClient.createComment(commentDto, userId, itemId);
    }
}
