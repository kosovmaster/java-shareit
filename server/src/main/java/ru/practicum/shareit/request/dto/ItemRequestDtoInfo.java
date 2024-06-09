package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class ItemRequestDtoInfo {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemDto> items;
}
