package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    Collection<UserDto> getAllUser(Integer from, Integer size);

    void deleteUserById(Long userId);
}
