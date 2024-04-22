package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exection.NotFoundException;
import ru.practicum.shareit.exection.NotUniqueEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceDtoImpl implements UserServiceDto {

    private final UserServiceDao userServiceDao;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        checkEmail(user);
        return UserMapper.toUserDto(userServiceDao.addUser(user));
    }

    @Override
    public UserDto findById(Integer id) {
        if (!isUserInMemory(id)) {
            throw new NotFoundException("Пользователь с id: " + id + "не найден");
        }
        User user = userServiceDao.findById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        if (!isUserInMemory(id)) {
            throw new NotFoundException("Пользователь с id: " + id + "не найден");
        }
        User user = new User();
        UserDto userFromMemory = findById(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        } else {
            user.setName(userFromMemory.getName());
        }
        if (userDto.getEmail() != null) {
            checkEmail(user);
            user.setEmail(userDto.getEmail());
        } else {
            user.setEmail(userFromMemory.getEmail());
        }
        user.setId(id);
        return UserMapper.toUserDto(userServiceDao.updateUser(id, user));
    }

    @Override
    public List<UserDto> findAll() {
        return userServiceDao.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer id) {
        if (isUserInMemory(id)) {
            userServiceDao.deleteUser(id);
        } else {
            throw new NotFoundException("Пользователь с id: " + id + "не найден");
        }
    }

    private void checkEmail(User user) {
        boolean isEmailNotUnique = userServiceDao.findAll().stream().anyMatch(thisUser -> thisUser.getEmail().equals(user.getEmail())
                && !thisUser.getId().equals(user.getId()));
        if (isEmailNotUnique) {
            throw new NotUniqueEmailException("Пользователь с такой электронной почтой уже существует");
        }
    }

    private boolean isUserInMemory(Integer id) {
        return userServiceDao.findAll().stream().anyMatch(user -> user.getId().equals(id));
    }
}
