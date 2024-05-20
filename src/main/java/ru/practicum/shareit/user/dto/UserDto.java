package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserDto {
    @Positive(groups = Update.class)
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 60, groups = Create.class)
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotEmpty(groups = Create.class)
    private String email;
}
