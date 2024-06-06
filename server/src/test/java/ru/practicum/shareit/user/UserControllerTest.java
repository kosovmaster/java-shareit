package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    private UserDto userDtoCreate;
    private UserDto userDtoUpdate;
    private UserDto userDto;
    private List<UserDto> userDtoList;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "Ivan", "ivan@email.com");
        userDtoCreate = new UserDto(null, "Ivan", "ivan@email.com");
        userDtoUpdate = new UserDto(1L, "Ivan Ivanov", "ivan@email.com");
        userDtoList = List.of(userDto, new UserDto(null, "John", "john@email.com"));
    }

    @DisplayName("Тест получения пользователя по id")
    @Test
    @SneakyThrows
    public void shouldGetUserById() {
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(status().isOk());

        verify(userService).getUserById(anyLong());
    }

    @DisplayName("Тест создания пользователя")
    @Test
    @SneakyThrows
    public void shouldCreateUser() {
        when(userService.createUser(any())).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(status().is(201));
        verify(userService).createUser(any());
    }

    @DisplayName("")
    @Test
    @SneakyThrows
    public void shouldUpdateUser() {
        when(userService.updateUser(anyLong(), any())).thenReturn(userDtoUpdate);
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDtoUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userDtoUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userDtoUpdate.getEmail()))
                .andExpect(status().isOk());
        verify(userService).updateUser(anyLong(), any());
    }

    @DisplayName("Тест на получение всех пользователей постранично")
    @Test
    @SneakyThrows
    public void shouldFindAllUser() {
        when(userService.getAllUser(anyInt(), anyInt())).thenReturn(userDtoList);
        mvc.perform(get("/users"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userDtoList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(userDtoList.get(0).getName()))
                .andExpect(jsonPath("$[0].email").value(userDtoList.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(userDtoList.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(userDtoList.get(1).getName()))
                .andExpect(jsonPath("$[1].email").value(userDtoList.get(1).getEmail()))
                .andExpect(status().isOk());
        verify(userService).getAllUser(anyInt(), anyInt());
    }

    @DisplayName("Тест удаления пользователя")
    @Test
    @SneakyThrows
    public void shouldDeleteUser() {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
        verify(userService).deleteUserById(anyLong());
    }
}
