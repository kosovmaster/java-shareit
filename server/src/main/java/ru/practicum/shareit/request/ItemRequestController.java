package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoInfo createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(HEADER_USER) Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getListOfRequestsForItemsUser(@RequestHeader(HEADER_USER) Long userId) {
        return itemRequestService.getListOfRequestsForItemsUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getAllItemRequests(@RequestParam(defaultValue = PAGE_FROM_DEFAULT)
                                                       Integer from,
                                                       @RequestParam(defaultValue = PAGE_SIZE_DEFAULT)
                                                       Integer size,
                                                       @RequestHeader(HEADER_USER) Long userId) {
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo getItemRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(HEADER_USER) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
