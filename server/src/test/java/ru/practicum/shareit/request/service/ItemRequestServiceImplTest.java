package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private ItemRequestDto itemRequestDtoCreateOne;
    private ItemRequestDto itemRequestDtoCreateTwo;


    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoTwoCreate = new UserDto(null, "Lisa", "lisa@mail.ru");
        itemRequestDtoCreateOne = new ItemRequestDto("need a saw");
        itemRequestDtoCreateTwo = new ItemRequestDto("need a rake");
    }

    @DisplayName("Должен создать запрос")
    @Test
    public void shouldCreateItemRequest() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemRequestDtoInfo requestDtoCreated = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoOne.getId());

        assertThat(requestDtoCreated.getDescription(), is(equalTo(itemRequestDtoCreateOne.getDescription())));
        assertThat(requestDtoCreated.getCreated(), notNullValue());

    }

    @DisplayName("Должен вернуть все запросы пользователя")
    @Test
    public void shouldGetListOfRequestsForItemsUser() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);

        ItemRequestDtoInfo requestDtoCreatedOne = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoOne.getId());
        ItemRequestDtoInfo requestDtoCreatedTwo = itemRequestService
                .createItemRequest(itemRequestDtoCreateTwo, userDtoTwo.getId());

        List<ItemRequestDtoInfo> result = itemRequestService.getListOfRequestsForItemsUser(userDtoOne.getId());

        assertThat(result, is(hasSize(1)));
        assertThat(result, is(contains(requestDtoCreatedOne)));
        assertThat(result, is(contains(not(requestDtoCreatedTwo))));
    }

    @DisplayName("Должен вернуть запросы, созданные другими пользователями")
    @Test
    public void shouldGetItemRequestsPageByPage() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);

        ItemRequestDtoInfo requestDtoCreatedOne = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoOne.getId());
        ItemRequestDtoInfo requestDtoCreatedTwo = itemRequestService
                .createItemRequest(itemRequestDtoCreateTwo, userDtoTwo.getId());

        List<ItemRequestDtoInfo> result = itemRequestService
                .getAllItemRequests(0, 2, userDtoOne.getId());

        assertThat(result, is(hasSize(1)));
        assertThat(result, is(contains(requestDtoCreatedTwo)));
        assertThat(result, is(contains(not(requestDtoCreatedOne))));
    }

    @DisplayName("Должен вернуть запрос по id")
    @Test
    public void shouldGetItemRequestById() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemRequestDtoInfo requestDtoCreated = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoOne.getId());

        ItemRequestDtoInfo requestDtoResult = itemRequestService
                .getItemRequestById(requestDtoCreated.getId(), userDtoOne.getId());

        assertThat(requestDtoCreated, is(equalTo(requestDtoResult)));
    }

    @DisplayName("Должен выдать исключение при попытке вернуть запрос по неправильному id")
    @Test
    public void shouldNotGetItemRequestById() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        long requestId = 10L;

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(10L, userDtoOne.getId())
        );
        assertEquals("Request id=" + requestId + " not found", exception.getMessage());
    }
}