package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class UserDto {
    @Positive(groups = Update.class)
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = Create.class)
    private String name;
    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}
