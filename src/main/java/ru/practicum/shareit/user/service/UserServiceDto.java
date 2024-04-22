package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserServiceDto {

    UserDto addUser(UserDto userDto);

    UserDto findById(Integer id);

    UserDto updateUser(Integer id, UserDto userDto);

    List<UserDto> findAll();

    void deleteUser(Integer id);
}
