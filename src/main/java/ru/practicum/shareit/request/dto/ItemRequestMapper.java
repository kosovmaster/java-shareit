package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester, LocalDateTime created) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(created)
                .build();
    }

    public List<ItemRequestDtoInfo> toItemRequestDtoInfoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDtoInfo)
                .collect(Collectors.toList());
    }

    public ItemRequestDtoInfo toItemRequestDtoInfo(ItemRequest itemRequest) {
        return ItemRequestDtoInfo.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() == null ? new ArrayList<>() : itemMapper.toItemDtoCollection(itemRequest.getItems()))
                .build();
    }
}
