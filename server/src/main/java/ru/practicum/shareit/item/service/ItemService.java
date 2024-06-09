package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.util.Collection;

public interface ItemService {
    ItemDtoInfo getItemDtoById(Long itemId, Long userId);

    Collection<ItemDtoInfo> getAllItemUser(Long userId, Integer from, Integer size);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    Collection<ItemDto> searchItems(String text, Long userId, Integer from, Integer size);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);
}
