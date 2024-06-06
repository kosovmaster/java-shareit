package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
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
        userDtoTwoCreate = new UserDto(null, "JohnIvan", "john@mail.ru");
        itemRequestDtoCreateOne = new ItemRequestDto("need shovel");
        itemDtoOneCreate = new ItemDto(null, "saw", "metal saw", true, null);
        itemDtoTwoCreate = new ItemDto(null, "hammer", "wooden hammer", true, null);
        itemDtoThreeCreate = new ItemDto(null, "shovel", "sand shovel", true, 1L);
        itemDto = new ItemDto(null, "saw", "metal saw", true, null);
        itemDtoUpdate = new ItemDto(null, "saw", "legendary metal saw", true, null);
        commentDtoCreate = new CommentDto(null, "nice", null, null, null);
        bookingDtoTwoCreate = new BookingDtoCreate(null, FIXED_TIME.plusNanos(1), FIXED_TIME.plusNanos(2));
        bookingDtoCreate = new BookingDtoCreate(null, FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2));
    }

    @DisplayName("Тест создания предмета")
    @Test
    public void createItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));
    }

    @DisplayName("Тест обновления предмета")
    @Test
    public void updateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));

        ItemDto itemDtoOneUpdated = itemService.updateItem(userDtoOne.getId(), itemDtoOne.getId(), itemDtoUpdate);
        itemDtoUpdate.setId(itemDtoOneUpdated.getId());

        assertThat(itemDtoOneUpdated, is(equalTo(itemDtoUpdate)));
    }

    @DisplayName("Тест на выдачу исключения, при попытке обновить вещь, не ее владельцем")
    @Test
    @SneakyThrows
    public void shouldNotUpdateItem() {
        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        UserDto userDto2 = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDtoOneCreate);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(userDto2.getId(), itemDto1.getId(), itemDtoUpdate));
        assertEquals("Предмет с данным id={}" + itemDto1.getId() + " не найден", exception.getMessage());
    }

    @DisplayName("Тест на выдачу исключения при попытке получить вещь с помощью неправильного id")
    @Test
    public void shouldNotGetItemDtoById() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        long itemId = 10L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.findItemById(userDtoOne.getId(), itemId));
        assertEquals("Предмет с данным id=" + itemId + " не найден", exception.getMessage());
    }

    @DisplayName("Тест на поиск предмета по тексту в имени или описании")
    @Test
    public void searchItem() {
        String text1 = "saw";
        String text2 = "wooden";

        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDtoOneCreate);

        Collection<ItemDto> items1 = itemService.searchItem(text1, 1L, 0, 2);
        Collection<ItemDto> items2 = itemService.searchItem(text2, 2L, 0, 2);

        assertThat(items1, is(hasSize(1)));
        assertThat(items1, is(contains(itemDto1)));
        assertThat(items2, is(hasSize(0)));
    }

    @DisplayName("Тест создания комментария")
    @Test
    public void createComment() {
        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        UserDto userDto2 = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto1.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDto2.getId(), bookingDtoTwoCreate);
        bookingService.updateBooking(userDto1.getId(), bookingDtoCreated.getId(), true);
        CommentDto commentDtoCreated = itemService.createComment(commentDtoCreate, userDto2.getId(), itemDto1.getId());

        assertThat(commentDtoCreated.getText(), is(equalTo(commentDtoCreated.getText())));
        assertThat(commentDtoCreated.getAuthorName(), is(equalTo(userDto2.getName())));
        assertThat(commentDtoCreated.getItemId(), is(equalTo(itemDto1.getId())));
        assertThat(commentDtoCreated.getCreated(), notNullValue());
    }

    @DisplayName("Не должен создавать комментарий к предмету, которого не существует")
    @Test
    public void shouldNotCreateCommentOnItemNotExist() {
        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        UserDto userDto2 = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto1.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDto2.getId(), bookingDtoTwoCreate);
        bookingService.updateBooking(userDto1.getId(), bookingDtoCreated.getId(), true);

        ValidationException exception = assertThrows(ValidationException.class, () -> itemService.createComment(commentDtoCreate, userDto2.getId(), 5555L));
        assertEquals("Предмет еще не существует", exception.getMessage());
    }

    @DisplayName("Не должен создавать комментарий, если время брони не истекло")
    @Test
    public void shouldNotCreateCommentIfTheBookingTimeHasNotExpired() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoCreate);
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        LocalDateTime startTime = bookingDtoCreated.getStart();
        LocalDateTime endTime = bookingDtoCreated.getEnd();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(endTime)) {
            try {
                itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId());
                fail("Expected ValidationException to be thrown");
            } catch (ValidationException e) {
                assertEquals("Only users whose booking has expired can leave comments", e.getMessage());
            }
        } else {
            itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId());
        }
    }

    @DisplayName("Тест на выдачу исключения при попытке создать вещь которой не существует")
    @Test
    public void shouldNotCreateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(userDtoOne.getId(), itemDtoThreeCreate)
        );
        String expectedPattern = "Запрос с данным id=1.*не найден";
        assertFalse(exception.getMessage().matches(expectedPattern));
    }


    @DisplayName("Тест на получение всех вещей пользователем")
    @Test
    public void getAllItemUser() {
        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        UserDto userDto2 = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDtoOneCreate);
        ItemDto itemDto2 = itemService.createItem(userDto2.getId(), itemDtoTwoCreate);

        List<ItemDtoInfo> itemDtoInfoListOne = new ArrayList<>(itemService.getAllItemUser(userDto1.getId(), 0, 2));
        List<ItemDtoInfo> itemDtoInfoListTwo = new ArrayList<>(itemService.getAllItemUser(userDto2.getId(), 0, 2));

        assertThat(itemDtoInfoListOne, is(hasSize(1)));
        assertThat(itemDtoInfoListOne.get(0).getName(), is(equalTo(itemDto1.getName())));
        assertThat(itemDtoInfoListOne.get(0).getDescription(), is(equalTo(itemDto1.getDescription())));
        assertThat(itemDtoInfoListTwo, is(hasSize(1)));
        assertThat(itemDtoInfoListTwo.get(0).getName(), is(equalTo(itemDto2.getName())));
        assertThat(itemDtoInfoListTwo.get(0).getDescription(), is(equalTo(itemDto2.getDescription())));
    }

    @DisplayName("Тест на показ всех вещей для его владельца, с датами бронирования пользователя")
    @Test
    public void getItemDtoInfoByIdForOwner() {
        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        UserDto userDto2 = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDto);

        bookingDtoTwoCreate.setItemId(itemDto1.getId());
        BookingDto bookingDtoCreate = bookingService.createBooking(userDto2.getId(), bookingDtoTwoCreate);
        bookingService.updateBooking(userDto1.getId(), bookingDtoCreate.getId(), true);

        itemService.createComment(commentDtoCreate, userDto2.getId(), itemDto1.getId());
        ItemDtoInfo result = itemService.findItemById(userDto1.getId(), itemDto1.getId());

        assertThat(result.getComments(), hasSize(1));
        assertThat(result.getComments().get(0).getText(), is(equalTo(commentDtoCreate.getText())));
        assertThat(result.getComments().get(0).getAuthorName(), is(equalTo(userDto2.getName())));
        assertThat(result.getComments().get(0).getItemId(), is(equalTo(itemDto1.getId())));
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), notNullValue());
        assertThat(result.getDescription(), is(equalTo(itemDto1.getDescription())));
        assertThat(result.getName(), is(equalTo(itemDto1.getName())));
    }

    @DisplayName("Тест на показ всех вещей без дат бронирования пользователя, который не является его владельцем")
    @Test
    public void getItemDtoInfoByIdNotOwner() {
        UserDto userDto1 = userService.createUser(userDtoOneCreate);
        UserDto userDto2 = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto1 = itemService.createItem(userDto1.getId(), itemDto);

        bookingDtoTwoCreate.setItemId(itemDto1.getId());
        BookingDto bookingDtoCreate = bookingService.createBooking(userDto2.getId(), bookingDtoTwoCreate);
        bookingService.updateBooking(userDto1.getId(), bookingDtoCreate.getId(), true);

        itemService.createComment(commentDtoCreate, userDto2.getId(), itemDto1.getId());
        ItemDtoInfo result = itemService.findItemById(userDto2.getId(), itemDto1.getId());

        assertThat(result.getComments(), hasSize(1));
        assertThat(result.getComments().get(0).getText(), is(equalTo(commentDtoCreate.getText())));
        assertThat(result.getComments().get(0).getAuthorName(), is(equalTo(userDto2.getName())));
        assertThat(result.getComments().get(0).getItemId(), is(equalTo(itemDto1.getId())));
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        assertThat(result.getDescription(), is(equalTo(itemDto1.getDescription())));
        assertThat(result.getName(), is(equalTo(itemDto1.getName())));
    }

    @DisplayName("Тест на создание предмета по запросу пользователя")
    @Test
    public void createItemForTheUserRequest() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);

        ItemRequestDtoInfo requestDtoCreated = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoTwo.getId());

        itemDtoOneCreate.setRequestId(requestDtoCreated.getId());
        itemDto.setRequestId(requestDtoCreated.getId());
        ItemDto itemDtoOne = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));
    }
}
