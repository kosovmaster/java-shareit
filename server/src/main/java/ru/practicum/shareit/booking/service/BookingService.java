package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getOneBookingUser(Long bookingId, Long userId);

    Collection<BookingDto> getAllBookingsBooker(Long userId, BookingState bookingState, Integer from, Integer size);

    Collection<BookingDto> getAllBookingsOwner(Long userId, BookingState bookingState, Integer from, Integer size);
}
