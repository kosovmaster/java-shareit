package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.validator.state.ValidState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDtoCreate bookingDtoCreate,
                                                @RequestHeader(USER_HEADER) Long userId) {
        log.info("POST: user request with id={} to create a booking, request body={}", userId, bookingDtoCreate);
        return bookingClient.createBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(USER_HEADER) Long userId,
                                                @PathVariable @Positive @NotNull Long bookingId,
                                                @RequestParam @NotNull Boolean approved) {
        log.info("PATCH: owner request with id={} to update a booking with id={} and parameter \"approved\"={}",
                userId, bookingId, approved);
        return bookingClient.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getOneBookingUser(@PathVariable @Positive @NotNull Long bookingId,
                                                    @RequestHeader(USER_HEADER) Long userId) {
        log.info("GET: user request with id={} to view a booking with id={}", userId, bookingId);
        return bookingClient.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsBooker(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(defaultValue = STATE_DEFAULT)
                                                       @ValidState String state,
                                                       @RequestParam(defaultValue = PAGE_FROM_DEFAULT)
                                                           @Min(0) Integer from,
                                                       @RequestParam(defaultValue = PAGE_SIZE_DEFAULT)
                                                           @Min(1) Integer size) {
        log.info("GET: user request with id={} to view a bookings with state={}. Page from={}, page size={}",
                userId, state, from, size);
        return bookingClient.getAllBookingsBooker(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOwner(@RequestHeader(USER_HEADER) Long userId,
                                                      @RequestParam(defaultValue = STATE_DEFAULT)
                                                      @ValidState String state,
                                                      @RequestParam(defaultValue = PAGE_FROM_DEFAULT)
                                                          @Min(0) Integer from,
                                                      @RequestParam(defaultValue = PAGE_SIZE_DEFAULT)
                                                          @Min(1) Integer size) {
        log.info("GET: owner request with id={} to view a bookings with state={}. Page from={}, page size={}",
                userId, state, from, size);
        return bookingClient.getAllBookingsOwner(userId, BookingState.valueOf(state), from, size);
    }
}
