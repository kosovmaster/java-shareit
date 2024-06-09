package ru.practicum.shareit.item.comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    private Long itemId;
}
