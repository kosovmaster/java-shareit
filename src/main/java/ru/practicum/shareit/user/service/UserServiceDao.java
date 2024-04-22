package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserServiceDao {

    User addUser(User user);

    User findById(Integer id);

    User updateUser(Integer id, User user);

    List<User> findAll();

    void deleteUser(Integer id);
}
