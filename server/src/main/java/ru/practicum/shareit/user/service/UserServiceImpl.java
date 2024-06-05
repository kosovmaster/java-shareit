package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

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
            User createdUser = userRepository.save(userMapper.toUser(userDto));
            return userMapper.toUserDto(createdUser);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMostSpecificCause().getMessage();
            log.warn("The user has not been created due to data integrity violation: {}", errorMessage);
            if (errorMessage != null && errorMessage.contains("PUBLIC.USERS(EMAIL")) {
                throw new ConflictException("The email " + userDto.getEmail() + " is already in exists");
            } else {
                throw new ConflictException("The user has not been created: " + userDto + ". Error: " + errorMessage);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).stream().findFirst().orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDtoNew) {
        User userOld = userRepository.findById(userId).stream().findFirst().orElseThrow(() -> {
            throw new ValidationException("Пользователь с id: " + userId + " уже существует");
        });

        getExceptionIfEmailExistsAndItIsAlien(userDtoNew.getEmail(), userOld.getEmail());
        User updatedUser = userRepository.save(setUser(userOld, userDtoNew));
        return userMapper.toUserDto(updatedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<UserDto> getAllUser(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.asc("id")));
        List<User> users = userRepository.findAll(pageable).getContent();
        return userMapper.toUserDtoCollection(users);
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        log.warn("Пользователь с id: " + userId + " был удален");
    }

    private void getExceptionIfEmailExistsAndItIsAlien(String emailNew, String emailOld) {
        if (emailNew == null) {
            return;
        }
        boolean isExistEmail = userRepository.existsByEmail(emailNew);
        if (isExistEmail && !emailNew.equals(emailOld)) {
            throw new ConflictException("Пользователь с email: " + emailNew + " уже существует");
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
