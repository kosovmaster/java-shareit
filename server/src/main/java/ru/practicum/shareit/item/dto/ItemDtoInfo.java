package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class ItemDtoInfo {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoInfo lastBooking;
    private BookingDtoInfo nextBooking;
    private List<CommentDto> comments;
}
