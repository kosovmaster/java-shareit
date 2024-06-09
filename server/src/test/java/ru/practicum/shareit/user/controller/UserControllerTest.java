package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.UserController;
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

    private UserDto userDtoOneCreate;
    private UserDto userDtoOne;
    private UserDto userDtoOneUpdate;
    private List<UserDto> userDtoList;

    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoOne = new UserDto(1L, "Ivan", "ivan@mail.ru");
        userDtoOneUpdate = new UserDto(1L, "Ivan Petrov", "ivan@mail.ru");
        userDtoList = List.of(userDtoOne, new UserDto(2L, "Lisa", "lisa@mail.ru"));
    }

    @DisplayName("Должен вернуть пользователя по id")
    @Test
    @SneakyThrows
    public void shouldGetUserById() {
        when(userService.getUserById(anyLong())).thenReturn(userDtoOne);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(jsonPath("$.id").value(userDtoOne.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOne.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOne.getEmail()))
                .andExpect(status().isOk());

        verify(userService).getUserById(anyLong());
    }

    @DisplayName("Должен вернуть определенное количество пользователей постранично")
    @Test
    @SneakyThrows
    public void shouldGetAllUser() {
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

    @DisplayName("Должен создать пользователя")
    @Test
    @SneakyThrows
    public void shouldCreateUser() {
        when(userService.createUser(any())).thenReturn(userDtoOne);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoOneCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDtoOne.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOne.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOne.getEmail()))
                .andExpect(status().is(201));

        verify(userService).createUser(any());
    }

    @DisplayName("Должен обновить пользователя")
    @Test
    @SneakyThrows
    public void shouldUpdateUser() {
        when(userService.updateUser(anyLong(), any())).thenReturn(userDtoOneUpdate);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoOneUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDtoOneUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOneUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOneUpdate.getEmail()))
                .andExpect(status().isOk());

        verify(userService).updateUser(anyLong(), any());
    }

    @DisplayName("Должен удалить пользователя")
    @Test
    @SneakyThrows
    public void shouldDeleteUserById() {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(anyLong());
    }
}