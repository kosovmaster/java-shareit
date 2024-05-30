package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validator.BookingDtoCreate;
import ru.practicum.shareit.booking.validator.BookingDtoInfo;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            throw new NullPointerException("booking cannot be null");
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemMapper.toItemDto(booking.getItem()))
                .booker(userMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Collection<BookingDto> toBookingDtoCollection(Collection<Booking> booking) {
        if (booking == null) {
            throw new NullPointerException("booking cannot be null");
        }

        return booking.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    public Booking toBooking(BookingDtoCreate bookingDtoCreate, User user, Item item) {
        if (bookingDtoCreate == null || user == null || item == null) {
            throw new NullPointerException("bookingDtoCreate, user, and item cannot be null");
        }

        return Booking.builder()
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .item(item)
                .booker(user)
                .status(WAITING)
                .build();
    }

    public BookingDtoInfo toBookingDtoInfo(Booking booking) {
        if (booking == null) {
            throw new NullPointerException("booking cannot be null");
        }

        return BookingDtoInfo.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
    }

    public List<BookingDtoInfo> toBookingDtoInfoList(List<Booking> bookings) {
        if (bookings == null) {
            throw new NullPointerException("bookings cannot be null");
        }

        return bookings.stream()
                .map(this::toBookingDtoInfo)
                .collect(Collectors.toList());
    }

    public Map<Long, BookingDtoInfo> toBookingDtoInfoMapByIdItem(List<BookingDtoInfo> booking) {
        if (booking == null) {
            throw new NullPointerException("booking cannot be null");
        }

        return booking.stream().collect(Collectors.toMap(
                BookingDtoInfo::getItemId, bookingDtoInfo -> bookingDtoInfo));
    }
}
