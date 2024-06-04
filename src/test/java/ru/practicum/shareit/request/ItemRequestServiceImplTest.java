package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.service.ItemRequestService;
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
    private UserDto userDtoFirst;
    private UserDto userDtoSecond;
    private ItemRequestDto itemRequestDtoFirst;
    private ItemRequestDto itemRequestDtoSecond;

    @BeforeEach
    public void setUp() {
        userDtoFirst = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoSecond = new UserDto(null, "John", "john@mail.ru");
        itemRequestDtoFirst = new ItemRequestDto("need shovel");
        itemRequestDtoSecond = new ItemRequestDto("need hammer");
    }

    @DisplayName("Тест создания запроса")
    @Test
    public void createItemRequestTest() {
        UserDto userDto = userService.createUser(userDtoFirst);
        ItemRequestDtoInfo request = itemRequestService.createItemRequest(itemRequestDtoFirst, userDto.getId());
        assertThat(request.getDescription(), is(equalTo(itemRequestDtoFirst.getDescription())));
        assertThat(request.getCreated(), notNullValue());
    }

    @DisplayName("Тест на возврат всех запросов пользователя")
    @Test
    public void getListOfRequestsForItemUserTest() {
        UserDto userDto = userService.createUser(userDtoFirst);
        UserDto userDto2 = userService.createUser(userDtoSecond);

        ItemRequestDtoInfo requestDtoInfo1 = itemRequestService.createItemRequest(itemRequestDtoFirst, userDto.getId());
        ItemRequestDtoInfo requestDtoInfo2 = itemRequestService.createItemRequest(itemRequestDtoSecond, userDto2.getId());

        List<ItemRequestDtoInfo> list = itemRequestService.getListOfRequestsForItemUser(userDto.getId());
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(contains(requestDtoInfo1)));
        assertThat(list, is(contains(not(requestDtoInfo2))));
    }

    @DisplayName("Тест на возврат запросов, созданных другими пользователями")
    @Test
    public void getItemRequestsPageByPageTest() {
        UserDto userDto = userService.createUser(userDtoFirst);
        UserDto userDto2 = userService.createUser(userDtoSecond);

        ItemRequestDtoInfo requestDtoInfo1 = itemRequestService.createItemRequest(itemRequestDtoFirst, userDto.getId());
        ItemRequestDtoInfo requestDtoInfo2 = itemRequestService.createItemRequest(itemRequestDtoSecond, userDto2.getId());

        List<ItemRequestDtoInfo> list = itemRequestService.getItemRequestsPageByPage(0, 2, userDto.getId());
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(contains(requestDtoInfo2)));
        assertThat(list, is(contains(not(requestDtoInfo1))));
    }

    @DisplayName("Тест на возврат запроса по id")
    @Test
    public void getItemRequestByIdTest() {
        UserDto userDto = userService.createUser(userDtoFirst);
        ItemRequestDtoInfo requestDtoInfo1 = itemRequestService.createItemRequest(itemRequestDtoFirst, userDto.getId());
        ItemRequestDtoInfo requestDtoResult = itemRequestService.getItemRequestById(requestDtoInfo1.getId(), userDto.getId());

        assertThat(requestDtoInfo1, is(equalTo(requestDtoResult)));
    }

    @DisplayName("Тест на исключение при неверном возврате запроса по id")
    @Test
    public void notGetItemRequestByIdTest() {
        UserDto userDto = userService.createUser(userDtoFirst);
        long requestId = 10L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(10L, userDto.getId()));
        assertEquals("Запрос с id = " + requestId + " не найден", exception.getMessage());
    }
}
