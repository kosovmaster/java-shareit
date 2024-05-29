package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDtoInfo findItemById(Long userId, Long itemId);

    public Collection<ItemDtoInfo> getAllItemUser(Long userId, Integer from, Integer size);

    Item getItemByIdAvailable(Long itemId, Long userId);

    public Collection<ItemDto> searchItem(String text, Integer from, Integer size);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);
}
