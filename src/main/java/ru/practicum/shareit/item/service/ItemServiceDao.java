package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemServiceDao {

    Item addItem(Item item);

    Optional<Item> findItemById(Integer id);

    Item updateItem(Item item);

    List<Item> findAll(Integer id);

    List<Item> searchItem(String text);
}
