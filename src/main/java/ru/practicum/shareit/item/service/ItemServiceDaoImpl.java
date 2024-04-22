package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemServiceDaoImpl implements ItemServiceDao {

    private final Map<Integer, List<Item>> items = new HashMap<>();
    private Integer id = 1;

    @Override
    public Item addItem(Item item) {
        item.setId(id);
        id++;
        List<Item> listItems = new ArrayList<>();
        listItems.add(item);
        items.put(item.getOwner(), listItems);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Integer itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public Item updateItem(Item item) {
        List<Item> userItems = items.get(item.getOwner());
        List<Item> toRemove = userItems.stream()
                .filter(userItem -> userItem.getId().equals(item.getId()))
                .collect(Collectors.toList());
        userItems.removeAll(toRemove);
        userItems.add(item);
        return item;
    }

    @Override
    public List<Item> findAll(Integer userId) {
        return new ArrayList<>(items.get(userId));
    }

    @Override
    public List<Item> searchItem(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) || item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
