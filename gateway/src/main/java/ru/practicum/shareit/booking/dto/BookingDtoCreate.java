package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.validator.create.ValidBookingCreate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@ValidBookingCreate
public class BookingDtoCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
