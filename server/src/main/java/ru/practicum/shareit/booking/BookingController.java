package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader(HEADER_USER) Long userId) {
        return bookingService.createBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(HEADER_USER) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOneBookingUser(@PathVariable Long bookingId,
                                        @RequestHeader(HEADER_USER) Long userId) {
        return bookingService.getOneBookingUser(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsBooker(@RequestHeader(HEADER_USER) Long userId,
                                                       @RequestParam(defaultValue = STATE_DEFAULT) String state,
                                                       @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                       @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return bookingService.getAllBookingsBooker(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsOwner(@RequestHeader(HEADER_USER) Long userId,
                                                      @RequestParam(defaultValue = STATE_DEFAULT) String state,
                                                      @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                      @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return bookingService.getAllBookingsOwner(userId, BookingState.valueOf(state), from, size);
    }
}
