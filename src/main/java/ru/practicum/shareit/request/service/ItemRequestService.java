package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoInfo createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoInfo> getListOfRequestsForItemUser(Long userId);

    List<ItemRequestDtoInfo> getItemRequestsPageByPage(Integer from, Integer size, Long userId);

    ItemRequestDtoInfo getItemRequestById(Long requestId, Long userId);
}
