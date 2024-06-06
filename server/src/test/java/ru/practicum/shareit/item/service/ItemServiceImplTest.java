package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Constant.FIXED_TIME;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private ItemRequestDto itemRequestDtoCreateOne;
    private ItemDto itemDtoOneCreate;
    private ItemDto itemDtoTwoCreate;
    private ItemDto itemDtoThreeCreate;
    private ItemDto itemDto;
    private ItemDto itemDtoUpdate;
    private CommentDto commentDtoCreate;
    private BookingDtoCreate bookingDtoTwoCreate;
    private BookingDtoCreate bookingDtoCreate;

    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoTwoCreate = new UserDto(null, "Lisa", "lisa@mail.ru");
        itemRequestDtoCreateOne = new ItemRequestDto("need a saw");
        itemDtoOneCreate = new ItemDto(null, "saw", "wood saw", true, null);
        itemDtoTwoCreate = new ItemDto(null, "rake", "leaf rake", true, null);
        itemDtoThreeCreate = new ItemDto(null, "rake", "leaf rake", true, 1L);
        itemDto = new ItemDto(null, "saw", "wood saw", true, null);
        itemDtoUpdate = new ItemDto(null, "saw", "cool wood saw", true, null);
        commentDtoCreate = new CommentDto(null, "good", null, null, null);
        bookingDtoTwoCreate = new BookingDtoCreate(null, FIXED_TIME.plusNanos(1), FIXED_TIME.plusNanos(2));
        bookingDtoCreate = new BookingDtoCreate(null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @DisplayName("Должен выдать исключение при попытке получить вещь по неправильному id")
    @Test
    public void shouldNotGetItemDtoById() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        long itemId = 10L;

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemDtoById(itemId, userDtoOne.getId())
        );
        assertEquals("The item with this id=" + itemId + " not found", exception.getMessage());
    }

    @DisplayName("Должен показать вещь с датами бронирования пользователю, который является его владельцем")
    @Test
    public void shouldGetItemDtoByIdOwner() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId());
        ItemDtoInfo itemDtoResult = itemService.getItemDtoById(itemDtoOne.getId(), userDtoOne.getId());

        assertThat(itemDtoResult.getComments(), hasSize(1));
        assertThat(itemDtoResult.getComments().get(0).getText(), is(equalTo(commentDtoCreate.getText())));
        assertThat(itemDtoResult.getComments().get(0).getAuthorName(), is(equalTo(userDtoTwo.getName())));
        assertThat(itemDtoResult.getComments().get(0).getItemId(), is(equalTo(itemDtoOne.getId())));
        assertThat(itemDtoResult.getNextBooking(), nullValue());
        assertThat(itemDtoResult.getLastBooking(), notNullValue());
        assertThat(itemDtoResult.getDescription(), is(equalTo(itemDtoOne.getDescription())));
        assertThat(itemDtoResult.getName(), is(equalTo(itemDtoOne.getName())));
    }

    @DisplayName("Должен показать вещь без дат бронирования пользователю, который не является его владельцем")
    @Test
    public void shouldGetItemDtoByIdNotOwner() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId());
        ItemDtoInfo itemDtoResult = itemService.getItemDtoById(itemDtoOne.getId(), userDtoTwo.getId());

        assertThat(itemDtoResult.getComments(), hasSize(1));
        assertThat(itemDtoResult.getComments().get(0).getText(), is(equalTo(commentDtoCreate.getText())));
        assertThat(itemDtoResult.getComments().get(0).getAuthorName(), is(equalTo(userDtoTwo.getName())));
        assertThat(itemDtoResult.getComments().get(0).getItemId(), is(equalTo(itemDtoOne.getId())));
        assertThat(itemDtoResult.getNextBooking(), nullValue());
        assertThat(itemDtoResult.getLastBooking(), nullValue());
        assertThat(itemDtoResult.getDescription(), is(equalTo(itemDtoOne.getDescription())));
        assertThat(itemDtoResult.getName(), is(equalTo(itemDtoOne.getName())));
    }

    @DisplayName("Должен показать список вещей пользователю, который является их владельцем")
    @Test
    public void shouldGetAllItemUser() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        ItemDto itemDtoTwo = itemService.createItem(itemDtoTwoCreate, userDtoTwo.getId());

        List<ItemDtoInfo> allItemsUserDtoOne = new ArrayList<>(itemService
                .getAllItemUser(userDtoOne.getId(), 0, 2));
        List<ItemDtoInfo> allItemsUserDtoTwo = new ArrayList<>(itemService
                .getAllItemUser(userDtoTwo.getId(), 0, 2));

        assertThat(allItemsUserDtoOne, is(hasSize(1)));
        assertThat(allItemsUserDtoOne.get(0).getName(), is(equalTo(itemDtoOne.getName())));
        assertThat(allItemsUserDtoOne.get(0).getDescription(), is(equalTo(itemDtoOne.getDescription())));
        assertThat(allItemsUserDtoTwo, is(hasSize(1)));
        assertThat(allItemsUserDtoTwo.get(0).getName(), is(equalTo(itemDtoTwo.getName())));
        assertThat(allItemsUserDtoTwo.get(0).getDescription(), is(equalTo(itemDtoTwo.getDescription())));
    }

    @DisplayName("Должен создать вещь")
    @Test
    public void shouldCreateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));
    }

    @DisplayName("Должен создать вещь под запрос пользователя")
    @Test
    public void shouldCreateItemForTheUserRequest() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);

        ItemRequestDtoInfo requestDtoCreated = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoTwo.getId());

        itemDtoOneCreate.setRequestId(requestDtoCreated.getId());
        itemDto.setRequestId(requestDtoCreated.getId());
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));
    }

    @DisplayName("Должен выдать исключение при попытке создать вещь с номером запроса, которого не существует")
    @Test
    public void shouldNotCreateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(itemDtoThreeCreate, userDtoOne.getId())
        );
        assertEquals("Request id=" + itemDtoThreeCreate.getRequestId() + " not found",
                exception.getMessage());
    }

    @DisplayName("Должен обновить вещь")
    @Test
    public void shouldUpdateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));

        ItemDto itemDtoOneUpdated = itemService.updateItem(itemDtoUpdate, itemDtoOne.getId(), userDtoOne.getId());
        itemDtoUpdate.setId(itemDtoOneUpdated.getId());

        assertThat(itemDtoOneUpdated, is(equalTo(itemDtoUpdate)));
    }

    @DisplayName("Должен выдать исключение при попытке обновить вещь не ее владельцем")
    @Test
    public void shouldNotUpdateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(itemDtoUpdate, itemDtoOne.getId(), userDtoTwo.getId())
        );
        assertEquals("The item with this id=" + itemDtoOne.getId() + " not found", exception.getMessage());
    }

    @DisplayName("Должен найти вещи по тексту в имени или описании")
    @Test
    public void shouldSearchItems() {
        String textOne = "wood";
        String textTwo = "rake";

        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        Collection<ItemDto> items = itemService.searchItems(textOne, 1L, 0, 2);
        Collection<ItemDto> itemsTwo = itemService.searchItems(textTwo, 2L, 0, 2);

        assertThat(items, is(hasSize(1)));
        assertThat(items, is(contains(itemDtoOne)));
        assertThat(itemsTwo, is(hasSize(0)));
    }

    @DisplayName("Должен создать комментарий")
    @Test
    public void shouldCreateComment() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);
        CommentDto commentDto = itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId());

        assertThat(commentDto.getText(), is(equalTo(commentDtoCreate.getText())));
        assertThat(commentDto.getItemId(), is(equalTo(itemDtoOne.getId())));
        assertThat(commentDto.getAuthorName(), is(equalTo(userDtoTwo.getName())));
        assertThat(commentDto.getCreated(), notNullValue());
    }

    @DisplayName("Не должен создавать комментарий к элементу, которого не существует")
    @Test
    public void shouldNotCreateCommentOnItemNotExist() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(commentDtoCreate, userDtoTwo.getId(), 54321L)
        );
        assertEquals("The item doesn't exist yet", exception.getMessage());
    }

    @DisplayName("Не должен создавать комментарий, если время бронирования не истекло")
    @Test
    public void shouldNotCreateCommentIfTheBookingTimeHasNotExpired() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId())
        );
        assertEquals("Only users whose booking has expired can leave comments", exception.getMessage());
    }
}