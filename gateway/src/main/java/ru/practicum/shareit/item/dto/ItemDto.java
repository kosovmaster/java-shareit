package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    @Positive(groups = Update.class)
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 60)
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 150)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;
}
