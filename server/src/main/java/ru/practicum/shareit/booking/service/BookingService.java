package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingDtoCreate bookingDtoCreate);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getOneBookingUser(Long bookingId, Long userId);

    Collection<BookingDto> getAllBookingBooker(Long userId, BookingState state, Integer from, Integer size);

    Collection<BookingDto> getAllBookingOwner(Long userId, BookingState state, Integer from, Integer size);
}
