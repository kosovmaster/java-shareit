package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    @Override
    public ItemRequestDtoInfo createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requester = getUserIfExist(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
        ItemRequest createdRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDtoInfo(createdRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoInfo> getListOfRequestsForItemUser(Long userId) {
        getUserIfExist(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);
        return itemRequestMapper.toItemRequestDtoInfoList(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(Integer from, Integer size, Long userId) {
        getUserIfExist(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("created")));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNot(userId, pageable);
        return itemRequestMapper.toItemRequestDtoInfoList(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoInfo getItemRequestById(Long requestId, Long userId) {
        getUserIfExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
        return itemRequestMapper.toItemRequestDtoInfo(itemRequest);
    }

    private User getUserIfExist(Long userId) {
        return userRepository.findById(userId).stream().findFirst().orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
