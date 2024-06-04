package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

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
    public ItemRequestDtoInfo createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(USER_HEADER) @Positive Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoInfo> getListRequestsForItemUser(@RequestHeader(USER_HEADER) @Positive Long userId) {
        return itemRequestService.getListOfRequestsForItemUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoInfo> getItemRequestsPageByPage(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                              @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                              @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getItemRequestsPageByPage(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoInfo getItemRequestById(@PathVariable @Positive Long requestId,
                                                 @RequestHeader(USER_HEADER) @Positive Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}