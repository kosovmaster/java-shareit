package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.Constant.PAGE_FROM_DEFAULT;
import static ru.practicum.shareit.Constant.PAGE_SIZE_DEFAULT;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUser(@RequestParam(defaultValue = PAGE_FROM_DEFAULT) @Min(0) Integer from,
                                             @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Min(1) Integer size) {
        log.info("GET: request to view a users. Page from={}, page size={}", from, size);
        return userClient.getAllUser(from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive @NotNull Long userId) {
        log.info("GET: request to view a user with id={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("POST: request to create a user, request body={}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive @NotNull Long userId,
                                             @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("PATCH: request to update a user, request body={}", userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive @NotNull Long userId) {
        log.info("DELETE: request to delete a user with id={}", userId);
        return userClient.deleteUserById(userId);
    }
}
