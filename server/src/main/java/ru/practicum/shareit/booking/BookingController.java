package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.Constant.*;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    public static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.createBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_HEADER) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOneBookingUser(@PathVariable Long bookingId,
                                        @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsBooker(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(defaultValue = STATE_DEFAULT) String state,
                                                       @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                       @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return bookingService.getAllBookingBooker(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsOwner(@RequestHeader(USER_HEADER) Long userId,
                                                      @RequestParam(defaultValue = STATE_DEFAULT) String state,
                                                      @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                      @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return bookingService.getAllBookingOwner(userId, BookingState.valueOf(state), from, size);
    }
}