package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemServiceDto itemServiceDto;


    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_HEADER) Integer userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST запрос на добавление пользователя с id - " + userId + " предмета " + itemDto.toString());
        return itemServiceDto.addItem(userId, itemDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_HEADER) Integer userId, @RequestBody ItemDto itemDto, @PathVariable Integer itemId) {
        log.info("PATCH запрос на обновление предмета с id - " + itemId + " у пользователя с id - " + userId);
        return itemServiceDto.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_HEADER) Integer userId) {
        log.info("GET запрос на получение вещей пользователя с id - " + userId);
        return itemServiceDto.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(USER_HEADER) Integer userId, @RequestParam(name = "text") String text) {
        log.info("GET запрос на поиск предметов");
        return itemServiceDto.searchItem(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_HEADER) Integer userId, @PathVariable("itemId") Integer itemId) {
        log.info("GET запрос на получение предмета с id - " + itemId + " пользователя с id - " + userId);
        return itemServiceDto.findItemById(userId, itemId);
    }
}
