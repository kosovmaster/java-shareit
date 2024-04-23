package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceDaoImpl implements UserServiceDao {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Integer idGenerator = 1;

    @Override
    public User addUser(User user) {
        checkEmail(user);
        user.setId(idGenerator);
        users.put(idGenerator, user);
        emails.add(user.getEmail());
        idGenerator++;
        return user;
    }

    @Override
    public User findById(Integer id) {
        checkUserInMemory(id);
        return users.get(id);
    }

    @Override
    public User updateUser(Integer id, User user) {
        checkUserInMemory(id);
        updateEmail(findById(id).getEmail(), user.getEmail());
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer id) {
        checkUserInMemory(id);
        emails.remove(findById(id).getEmail());
        users.remove(id);
    }

    private void checkEmail(User user) {
        if (emails.contains(user.getEmail())) {
            throw new NotUniqueEmailException("Пользователь с такой электронной почтой уже существует");
        }
    }

    private void checkUserInMemory(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
    }

    private void updateEmail(String oldEmail, String newEmail) {
        emails.remove(oldEmail);
        if (emails.contains(newEmail)) {
            emails.add(oldEmail);
            throw new NotUniqueEmailException("Пользователь с такой электронной почтой уже существует");
        }
    }
}
