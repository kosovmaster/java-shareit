package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemServiceDto {
    ItemDto addItem(Integer id, ItemDto itemDto);

    ItemDto findItemById(Integer userId, Integer itemId);

    List<ItemDto> findAll(Integer userId);

    List<ItemDto> searchItem(Integer userId, String text);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);
}
