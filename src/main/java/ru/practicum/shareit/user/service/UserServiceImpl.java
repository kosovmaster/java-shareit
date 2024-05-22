package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            User createdUser = userRepository.saveAndFlush(userMapper.toUser(userDto));
            log.info("Пользователь был создан={}", createdUser);
            return userMapper.toUserDto(createdUser);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserDtoById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).stream().findFirst().orElse(null);
        if (user == null) {
            throw new ValidationException("Пользователь с id: " + userId + " уже существует");
        }
        isExistEmail(userDto.getEmail(), user.getEmail());
        User updatedUser = userRepository.save(setUser(user, userDto));
        return userMapper.toUserDto(updatedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<UserDto> getAllUser() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserDtoCollection(users);
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        log.warn("Пользователь с id: " + userId + " был удален");
    }

    private void isExistEmail(String emailNew, String emailOld) {
        if (userRepository.existsByEmail(emailNew) && !emailNew.equals(emailOld)) {
            throw new ConflictException("Пользователь с такой электронной почтой = " + emailNew + " уже существует");
        }
    }

    private User setUser(User userOld, UserDto userDtoNew) {
        if (userDtoNew.getName() != null && !userDtoNew.getName().isEmpty()) {
            userOld.setName(userDtoNew.getName());
        }
        if (userDtoNew.getEmail() != null && !userDtoNew.getEmail().isEmpty()) {
            userOld.setEmail(userDtoNew.getEmail());
        }
        return userOld;
    }
}
