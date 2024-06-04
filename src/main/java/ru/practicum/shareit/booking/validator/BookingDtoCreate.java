package ru.practicum.shareit.booking.validator;

import lombok.*;
import ru.practicum.shareit.booking.validator.create.ValidBookingCreate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@ValidBookingCreate
public class BookingDtoCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
