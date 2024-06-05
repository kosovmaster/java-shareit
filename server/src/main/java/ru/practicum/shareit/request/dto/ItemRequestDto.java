package ru.practicum.shareit.request.dto;

import lombok.*;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {
    private String description;
}