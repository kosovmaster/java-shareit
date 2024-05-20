package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validator.BookingDtoCreate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingDto createBooking(Long userId, BookingDtoCreate bookingDtoCreate) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemByIdAvailable(bookingDtoCreate.getItemId(), userId);
        isBooker(userId, item);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoCreate, booker, item));
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = isBookingExistAndNotWaiting(userId, bookingId);
        BookingStatus status = approved ? APPROVED : REJECTED;
        booking.setStatus(status);
        isOwner(userId, booking);
        Booking bookingUpdated = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(bookingUpdated);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getOneBookingUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBookingByIdAndUser(bookingId, userId).orElseThrow(() -> new NotFoundException("Id резерва = " + bookingId + " не найден"));
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingBooker(Long userId, BookingState state) {
        if (state == null) {
            throw new ValidationException("Статус не может быть null");
        }
        userService.getUserById(userId);
        Collection<Booking> bookings = getBookingsForBooker(state, userId);
        return bookingMapper.toBookingDtoCollection(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingOwner(Long userId, BookingState state) {
        if (state == null) {
            throw new ValidationException("Статус не может быть null");
        }
        userService.getUserById(userId);
        Collection<Booking> bookings = getBookingsForOwner(state, userId);
        return bookingMapper.toBookingDtoCollection(bookings);
    }

    private void isBooker(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может зарезервировать этот предмет");
        }
    }

    private void isOwner(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("id = " + booking.getId() + " не найден");
        }
    }

    private Collection<Booking> getBookingsForOwner(BookingState state, Long userId) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, current);
            case FUTURE:
                return bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, current);
            case WAITING:
                return bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, REJECTED);
            case CURRENT:
                return bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, current, current);
        }
        return bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId);
    }

    private Collection<Booking> getBookingsForBooker(BookingState state, Long userId) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, current);
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, current);
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, REJECTED);
            case CURRENT:
                return bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, current, current);
        }
        return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
    }

    private Booking isBookingExistAndNotWaiting(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("id = " + bookingId + " не найден"));
        if (!booking.getStatus().equals(WAITING)) {
            throw new ValidationException("Статус зарезервированного предмета не WAITING");
        }
        return booking;
    }
}
