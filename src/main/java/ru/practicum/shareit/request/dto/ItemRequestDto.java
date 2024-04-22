package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {
    Integer id;
    String description;
    User request;
    LocalDate created;
}
