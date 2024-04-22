package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class BookingDto {
    private Integer id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private String booker;
    private Status status;
}
