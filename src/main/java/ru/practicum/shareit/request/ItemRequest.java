package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@Builder
public class ItemRequest {
    private Integer id;
    private String description;
    private User request;
    private LocalDateTime created;
}
