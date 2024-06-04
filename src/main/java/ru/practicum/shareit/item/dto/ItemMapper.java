package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.validator.BookingDtoInfo;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(Objects.requireNonNullElse(item.getName(), ""))
                .description(Objects.requireNonNullElse(item.getDescription(), ""))
                .available(item.getAvailable() != null ? item.getAvailable() : true)
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        if (itemDto == null || owner == null) {
            return null;
        }

        return Item.builder()
                .id(itemDto.getId())
                .name(Objects.requireNonNullElse(itemDto.getName(), ""))
                .description(Objects.requireNonNullElse(itemDto.getDescription(), ""))
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : true)
                .owner(owner)
                .request(itemRequest)
                .build();
    }

    public ItemDtoInfo toOneItemDtoInfoForAllUsers(Item item, List<CommentDto> comments) {
        if (item == null) {
            return null;
        }

        return ItemDtoInfo.builder()
                .id(item.getId())
                .name(Objects.requireNonNullElse(item.getName(), ""))
                .description(Objects.requireNonNullElse(item.getDescription(), ""))
                .available(item.getAvailable() != null ? item.getAvailable() : true)
                .comments(comments != null ? comments : Collections.emptyList())
                .build();
    }

    public ItemDtoInfo toOneItemDtoInfoForOwner(Item item, BookingDtoInfo next, BookingDtoInfo last, List<CommentDto> comments) {
        if (item == null) {
            return null;
        }

        return ItemDtoInfo.builder()
                .id(item.getId())
                .name(Objects.requireNonNullElse(item.getName(), ""))
                .description(Objects.requireNonNullElse(item.getDescription(), ""))
                .available(item.getAvailable() != null ? item.getAvailable() : true)
                .nextBooking(next)
                .lastBooking(last)
                .comments(comments != null ? comments : Collections.emptyList())
                .build();
    }

    public Collection<ItemDto> toItemDtoCollection(Collection<Item> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }
}
