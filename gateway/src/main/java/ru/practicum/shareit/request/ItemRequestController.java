package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(USER_HEADER) @Positive Long userId) {
        log.info("POST: user request with id={} to create a item request, request body={}", userId, itemRequestDto);
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfRequestsForItemsUser(@RequestHeader(USER_HEADER) @Positive Long userId) {
        log.info("GET: user request with id={} to view the list of his requests for elements", userId);
        return itemRequestClient.getListOfRequestsForItemsUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestParam(defaultValue = PAGE_FROM_DEFAULT)
                                                     @Min(0) Integer from,
                                                     @RequestParam(defaultValue = PAGE_SIZE_DEFAULT)
                                                     @Min(1) Integer size,
                                                     @RequestHeader(USER_HEADER) Long userId) {
        log.info("GET: user request with id={} to view a all items requests. Page from={}, page size={}",
                userId, from, size);
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable @Positive Long requestId,
                                                     @RequestHeader(USER_HEADER) @Positive Long userId) {
        log.info("GET: user request with id={} to view a item request with id={}", userId, requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}