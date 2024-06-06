package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.Constant.PAGE_FROM_DEFAULT;
import static ru.practicum.shareit.Constant.PAGE_SIZE_DEFAULT;

/**
 * TODO Sprint add-item-requests.
 */

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    public static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoInfo createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getListRequestsForItemUser(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getListOfRequestsForItemUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(@RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                              @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size,
                                                              @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getItemRequestsPageByPage(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo getItemRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}