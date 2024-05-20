package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserDtoById(Long userId);

    User getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    Collection<UserDto> getAllUser();

    void deleteUserById(Long userId);
}
