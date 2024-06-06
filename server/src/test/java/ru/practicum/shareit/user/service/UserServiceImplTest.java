package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplTest {
    private final UserService userService;
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private UserDto userDtoOne;
    private UserDto userDtoCreateDuplicateEmail;
    private UserDto userDtoOneUpdate;
    private UserDto userDtoOneUpdateDuplicateEmail;

    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoTwoCreate = new UserDto(null, "Lisa", "lisa@mail.ru");
        userDtoOne = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoCreateDuplicateEmail = new UserDto(null, "Galina", "ivan@mail.ru");
        userDtoOneUpdate = new UserDto(null, "Ivan Petrov", "ivanPetrow@mail.ru");
        userDtoOneUpdateDuplicateEmail = new UserDto(1L, "Ivan Petrov", "lisa@mail.ru");
    }

    @DisplayName("Должен вернуть пользователя по id")
    @Test
    public void shouldGetUserById() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoResult = userService.getUserById(userDtoCreated.getId());

        assertThat(userDtoCreated, is(equalTo(userDtoResult)));
    }

    @DisplayName("Должен выдать исключение при попытке вернуть пользователя по неправильному id")
    @Test
    public void shouldNotGetUserById() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(10L)
        );
        assertEquals("User with id=10 not found", exception.getMessage());
    }

    @DisplayName("Должен вернуть определенное количество пользователей постранично")
    @Test
    public void shouldGetAllUser() {
        UserDto userDtoCreatedOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoCreatedTwo = userService.createUser(userDtoTwoCreate);

        Collection<UserDto> allUsers = List.of(userDtoCreatedOne, userDtoCreatedTwo);
        Collection<UserDto> allUsersResult = userService.getAllUser(0, 2);

        assertThat(allUsers, is(equalTo(allUsersResult)));
        assertThat(allUsersResult, hasSize(2));
    }

    @DisplayName("Должен создать пользователя")
    @Test
    public void shouldCreateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        userDtoOne.setId(userDtoCreated.getId());

        assertThat(userDtoCreated, is(equalTo(userDtoOne)));
    }

    @DisplayName("Не должен создать пользователя с дублирующимся email")
    @Test
    public void shouldNotCreateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        userDtoOne.setId(userDtoCreated.getId());

        assertThat(userDtoCreated, is(equalTo(userDtoOne)));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.createUser(userDtoCreateDuplicateEmail)
        );

        assertEquals("The email " + userDtoCreateDuplicateEmail.getEmail() + " is already in exists",
                exception.getMessage());
    }

    @DisplayName("Должен обновить пользователя")
    @Test
    public void shouldUpdateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoUpdated = userService.updateUser(userDtoCreated.getId(), userDtoOneUpdate);
        userDtoOneUpdate.setId(userDtoCreated.getId());

        assertThat(userDtoUpdated, is(equalTo(userDtoOneUpdate)));
    }

    @DisplayName("Должен выдать исключение при попытке обновить пользователя по неправильному id")
    @Test
    public void shouldNotUpdateUser() {
        long id = 1;

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(id, userDtoOne)
        );
        assertEquals("The user with this id=" + id + " not already exists", exception.getMessage());
    }

    @DisplayName("Должен выдать исключение при попытке обновить пользователя с новым email, " +
            "который уже есть в базе данных у другого пользователя")
    @Test
    public void shouldNotUpdateUserEmail() {
        UserDto userOne = userService.createUser(userDtoOneCreate);
        userService.createUser(userDtoTwoCreate);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.updateUser(userOne.getId(), userDtoOneUpdateDuplicateEmail)
        );
        assertEquals("The user with this email=" + userDtoOneUpdateDuplicateEmail.getEmail()
                + " already exists", exception.getMessage());
    }

    @DisplayName("Должен удалить пользователя")
    @Test
    public void shouldDeleteUserById() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoResultOne = userService.getUserById(userDtoCreated.getId());

        assertThat(userDtoCreated, is(equalTo(userDtoResultOne)));

        userService.deleteUserById(userDtoCreated.getId());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(userDtoCreated.getId())
        );
        assertEquals("User with id=" + userDtoCreated.getId() + " not found", exception.getMessage());
    }
}