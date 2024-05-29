package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.validator.BookingDtoCreate;
import ru.practicum.shareit.booking.validator.state.ValidState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;

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
    public BookingDto createBooking(@Valid @RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.createBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_HEADER) Long userId,
                                    @PathVariable @Positive @NotNull Long bookingId,
                                    @RequestParam @NotNull Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOneBookingUser(@PathVariable @Positive @NotNull Long bookingId,
                                        @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsBooker(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "ALL") @ValidState String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return bookingService.getAllBookingBooker(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsOwner(@RequestHeader(USER_HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") @ValidState String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return bookingService.getAllBookingOwner(userId, BookingState.valueOf(state), from, size);
    }
}