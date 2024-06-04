package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {
    @Positive(groups = Update.class)
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 60, groups = Create.class)
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;
}
