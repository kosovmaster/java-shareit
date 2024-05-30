package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CommentDto {
    @Positive
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
    private Long itemId;
}
