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
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        User booker = getUserIfTheExists(userId);
        Item item = getAvailableItemByIdIfItExists(bookingDtoCreate.getItemId(), userId);

        getExceptionIfUserIsNotBooker(userId, item);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoCreate, booker, item));
        log.info("User id={} created booking id={} : {}", userId, booking.getId(), bookingDtoCreate);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking bookingOld = getBookingNotWaitingIfItExists(userId, bookingId);
        BookingStatus status = approved ? APPROVED : REJECTED;
        bookingOld.setStatus(status);
        getExceptionIfUserIsNotOwner(userId, bookingOld);

        Booking bookingUpdated = bookingRepository.save(bookingOld);
        log.info("Owner item updated status booking id={} to : {}", userId, status);
        return bookingMapper.toBookingDto(bookingUpdated);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getOneBookingUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBookingByIdAndUser(bookingId, userId).orElseThrow(() -> {
            log.warn("The booking with this id={} not found", bookingId);
            throw new NotFoundException("The booking with this id=" + bookingId + " not found");
        });
        log.info("Information about the booking id={} was obtained by the user id={}", bookingId, userId);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingsBooker(Long userId, BookingState bookingState,
                                                       Integer from, Integer size) {
        getUserIfTheExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.desc("start")));
        Collection<Booking> allBookings = getBookingsForBooker(bookingState, userId, pageable);
        log.info("Information about the bookings was obtained by the booker id={}", userId);
        return bookingMapper.toBookingDtoCollection(allBookings);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getAllBookingsOwner(Long userId, BookingState bookingState,
                                                      Integer from, Integer size) {
        getUserIfTheExists(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Order.desc("start")));
        Collection<Booking> allBookings = getBookingsForOwner(bookingState, userId, pageable);
        log.info("Information about the bookings was obtained by the owner id={}", userId);
        return bookingMapper.toBookingDtoCollection(allBookings);
    }

    private User getUserIfTheExists(Long userId) {
        return userRepository.findById(userId).stream().findFirst().orElseThrow(() -> {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        });
    }

    private Item getAvailableItemByIdIfItExists(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("The item with this id={} not found for user id={}", itemId, userId);
            throw new NotFoundException("The item with this id=" + itemId + " not found");
        });

        if (item.getAvailable().equals(false)) {
            log.warn("The item with id={} not found or not available", itemId);
            throw new ValidationException("The item with this id=" + itemId + " not found or not available");
        }
        return item;
    }

    private Booking getBookingNotWaitingIfItExists(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Booking id={} user id={} not found", bookingId, userId);
            return new NotFoundException("Booking with id=" + bookingId + " not found");
        });

        if (!booking.getStatus().equals(WAITING)) {
            log.warn("Booking id={} status is not WAITING. Booking status is = {}", bookingId, booking.getStatus());
            throw new ValidationException("Booking status is not WAITING");
        }
        return booking;
    }

    private void getExceptionIfUserIsNotOwner(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("User id={} for booking id={} is not the owner", userId, booking.getId());
            throw new NotFoundException("Booking id=" + booking.getId() + " not found");
        }
    }

    private void getExceptionIfUserIsNotBooker(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            log.warn("The owner id={} is trying to reserve his item id={}", userId, item.getOwner().getId());
            throw new NotFoundException("The owner cannot booking his item");
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
}
