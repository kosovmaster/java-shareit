package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
        userDtoOneCreate = new UserDto(null, "Ivan", "ivan@mail.com");
        userDtoTwoCreate = new UserDto(null, "John", "john@mail.com");
        userDtoOne = new UserDto(null, "Ivan", "ivan@mail.com");
        userDtoOneUpdate = new UserDto(null, "Ivan Ivanov", "ivanivanov@mail.com");
        userDtoCreateDuplicateEmail = new UserDto(null, "Sara", "ivan@mail.com");
        userDtoOneUpdateDuplicateEmail = new UserDto(1L, "Ivan Ivanov", "john@mail.com");
    }

    @DisplayName("Тест создания пользователя")
    @Test
    public void createUser() {
        UserDto userDtoCreate = userService.createUser(userDtoOneCreate);
        userDtoOne.setId(userDtoCreate.getId());
        assertThat(userDtoCreate, is(equalTo(userDtoOne)));
    }

    @DisplayName("Тест получения пользователя по id")
    @Test
    public void getUserById() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoResult = userService.getUserById(userDtoCreated.getId());
        assertThat(userDtoCreated, is(equalTo(userDtoResult)));
    }

    @DisplayName("Тест обновления пользователя")
    @Test
    public void updateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoUpdated = userService.updateUser(userDtoCreated.getId(), userDtoOneUpdate);

        assertThat(userDtoUpdated.getName(), is(equalTo(userDtoOneUpdate.getName())));
        assertThat(userDtoUpdated.getEmail(), is(equalTo(userDtoOneUpdate.getEmail())));
    }

    @DisplayName("Тест удаления пользователя по id")
    @Test
    public void deleteUserById() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoResult = userService.getUserById(userDtoCreated.getId());
        assertThat(userDtoCreated, is(equalTo(userDtoResult)));

        userService.deleteUserById(userDtoCreated.getId());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(userDtoCreated.getId()));
        assertEquals("Пользователь с id = " + userDtoCreated.getId() + " не найден", exception.getMessage());
    }

    @DisplayName("Тест на невозможность создания пользователя с дублирующим email")
    @Test
    public void shouldNotCreateUserWithDuplicateEmail() {
        UserDto userDtoCreate = userService.createUser(userDtoOneCreate);
        userDtoOne.setId(userDtoCreate.getId());
        assertThat(userDtoCreate, is(equalTo(userDtoOne)));

        ConflictException exception = assertThrows(ConflictException.class, () -> userService.createUser(userDtoCreateDuplicateEmail));
        assertEquals("The email " + userDtoCreateDuplicateEmail.getEmail() + " is already in exists", exception.getMessage());
    }

    @DisplayName("Тест на получение исключения при обновлении пользователя по неправильному id")
    @Test
    public void shouldNotUpdateUser() {
        long id = 1;

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.updateUser(id, userDtoOne));
        assertEquals("Пользователь с id: " + id + " уже существует", exception.getMessage());
    }

    @DisplayName("Тест на получение исключения при обновлении пользователя c неверным email или который уже есть в базе данных")
    @Test
    public void shouldNotUpdateUserEmail() {
        UserDto userDto = userService.createUser(userDtoOneCreate);
        userService.createUser(userDtoTwoCreate);

        ConflictException exception = assertThrows(ConflictException.class, () -> userService.updateUser(userDto.getId(), userDtoOneUpdateDuplicateEmail));
        assertEquals("Пользователь с email: " + userDtoOneUpdateDuplicateEmail.getEmail() + " уже существует", exception.getMessage());
    }
}
