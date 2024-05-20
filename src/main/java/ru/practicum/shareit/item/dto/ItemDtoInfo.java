package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.validator.BookingDtoInfo;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ItemDtoInfo {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private BookingDtoInfo nextBooking;
    private BookingDtoInfo lastBooking;
    private List<CommentDto> comments;
}
