package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto createBooking(Long userId, BookingDtoCreate bookingDtoCreate) {
        User booker = getUserIfExists(userId);
        Item item = getAvailableItemByIdIfItExists(bookingDtoCreate.getItemId(), userId);
        getExceptionIfUserIsNotBooker(userId, item);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoCreate, booker, item));
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingNotWaitingIfItExists(userId, bookingId);
        BookingStatus status = approved ? APPROVED : REJECTED;
        booking.setStatus(status);
        getExceptionIfUserIsNotOwner(userId, booking);
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
    public Collection<BookingDto> getAllBookingBooker(Long userId, BookingState state, Integer from, Integer size) {
        if (state == null) {
            throw new ValidationException("Статус не может быть null");
        }
        getUserIfExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        Collection<Booking> bookings = getBookingsForBooker(state, userId, pageable);
        return bookingMapper.toBookingDtoCollection(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingOwner(Long userId, BookingState state, Integer from, Integer size) {
        if (state == null) {
            throw new ValidationException("Статус не может быть null");
        }
        getUserIfExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        Collection<Booking> bookings = getBookingsForOwner(state, userId, pageable);
        return bookingMapper.toBookingDtoCollection(bookings);
    }

    private void getExceptionIfUserIsNotBooker(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            log.warn("The owner id={} is trying to reserve his item id={}", userId, item.getOwner().getId());
            throw new NotFoundException("The owner cannot booking his item");
        }
    }

    private void getExceptionIfUserIsNotOwner(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("id = " + booking.getId() + " не найден");
        }
    }

    private Collection<Booking> getBookingsForOwner(BookingState state, Long userId, Pageable pageable) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByItem_Owner_IdAndEndBefore(userId, current, pageable);
            case FUTURE:
                return bookingRepository.findAllByItem_Owner_IdAndStartAfter(userId, current, pageable);
            case WAITING:
                return bookingRepository.findAllByItem_Owner_IdAndStatus(userId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByItem_Owner_IdAndStatus(userId, REJECTED, pageable);
            case CURRENT:
                return bookingRepository
                        .findAllByItem_Owner_IdAndStartBeforeAndEndAfter(userId, current, current, pageable);
        }
        return bookingRepository.findAllByItem_Owner_Id(userId, pageable);
    }

    private Collection<Booking> getBookingsForBooker(BookingState state, Long userId, Pageable pageable) {
        LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBefore(userId, current, pageable);
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfter(userId, current, pageable);
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, REJECTED, pageable);
            case CURRENT:
                return bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfter(userId, current, current, pageable);
        }
        return bookingRepository.findAllByBooker_Id(userId, pageable);
    }

    private User getUserIfExists(Long userId) {
        return userRepository.findById(userId).stream().findFirst().orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Booking getBookingNotWaitingIfItExists(Long userId, Long bookingId) {
        log.warn("Booking id={} user id={} not found", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Резевр с id = " + bookingId + " не найден"));
        if (!booking.getStatus().equals(WAITING)) {
            throw new ValidationException("Статус резерва не WAITING");
        }
        return booking;
    }

    private Item getAvailableItemByIdIfItExists(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с данным id = " + itemId + " не найден"));
        if (item.getAvailable().equals(false)) {
            throw new ValidationException("Предмет с данным id = " + itemId + " не найден или не доступен");
        }
        return item;
    }
}
