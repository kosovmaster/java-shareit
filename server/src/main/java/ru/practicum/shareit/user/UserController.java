package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static ru.practicum.shareit.Constant.PAGE_FROM_DEFAULT;
import static ru.practicum.shareit.Constant.PAGE_SIZE_DEFAULT;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUser(@RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                          @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return userService.getAllUser(from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}