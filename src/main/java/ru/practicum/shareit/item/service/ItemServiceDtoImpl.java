package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exection.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceDaoImpl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceDtoImpl implements ItemServiceDto {

    private final ItemServiceDao itemServiceDao;
    private final UserServiceDaoImpl userServiceDao;

    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        UserDto user = UserMapper.toUserDto(userServiceDao.findById(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner((UserMapper.toUser(user)).getId());
        return ItemMapper.toItemDto(itemServiceDao.addItem(item));
    }

    @Override
    public ItemDto findItemById(Integer userId, Integer itemId) {
        userServiceDao.findById(userId);
        Optional<Item> itemGet = itemServiceDao.findItemById(itemId);
        if (itemGet.isEmpty()) {
            throw new NotFoundException(String.format("У пользователя с id %s не " +
                    "существует вещи с id %s", userId, itemId));
        }
        return ItemMapper.toItemDto(itemGet.get());
    }

    @Override
    public List<ItemDto> findAll(Integer userId) {
        userServiceDao.findById(userId);
        List<Item> itemList = itemServiceDao.findAll(userId);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(Integer userId, String text) {
        userServiceDao.findById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemServiceDao.searchItem(text);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        UserMapper.toUserDto(userServiceDao.findById(userId));
        Optional<Item> itemOptional = itemServiceDao.findItemById(itemId);
        if (itemOptional.isPresent()) {
            if (!itemOptional.get().getOwner().equals(userId)) {
                throw new NotFoundException(String.format("Пользователь с id %s " +
                        "не является владельцем вещи id %s.", userId, itemId));
            }
            Item storageItem = itemOptional.get();
            Item item = ItemMapper.toItem(itemDto);
            if (Objects.isNull(item.getAvailable())) {
                item.setAvailable(storageItem.getAvailable());
            }
            if (Objects.isNull(item.getDescription())) {
                item.setDescription(storageItem.getDescription());
            }
            if (Objects.isNull(item.getName())) {
                item.setName(storageItem.getName());
            }
            item.setId(storageItem.getId());
            item.setRequest(storageItem.getRequest());
            item.setOwner(storageItem.getOwner());
            return ItemMapper.toItemDto(itemServiceDao.updateItem(item));
        }
        return itemDto;
    }
}
