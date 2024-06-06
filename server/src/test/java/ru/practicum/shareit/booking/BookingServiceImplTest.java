package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.BookingState.WAITING;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private ItemDto itemDtoOneCreate;
    private ItemDto itemDtoCreate;
    private BookingDtoCreate bookingDtoCreate;
    private BookingDtoCreate bookingDtoTwoCreate;
    private final LocalDateTime current = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "Ivan", "ivan@mail.ru");
        userDtoTwoCreate = new UserDto(null, "John", "john@mail.ru");
        itemDtoOneCreate = new ItemDto(null, "shovel", "sand shovel", true, null);
        itemDtoCreate = new ItemDto(null, "hammer", "wooden hammer", false, null);
        bookingDtoCreate = new BookingDtoCreate(null, current.plusDays(1), current.plusDays(5));
        bookingDtoTwoCreate = new BookingDtoCreate(null, current.minusHours(20), current.minusHours(2));
    }

    @DisplayName("Тест создания бронирования")
    @Test
    public void createBookingTest() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoCreate);

        assertThat(bookingDtoCreated.getItem(), is(equalTo(itemDtoOne)));
        assertThat(bookingDtoCreated.getBooker(), is(equalTo(userDtoTwo)));
        assertThat(bookingDtoCreated.getStart(), is(equalTo(bookingDtoCreate.getStart())));
        assertThat(bookingDtoCreated.getEnd(), is(equalTo(bookingDtoCreate.getEnd())));
        assertThat(bookingDtoCreated.getStatus(), is(equalTo(BookingStatus.WAITING)));
    }

    @DisplayName("Тест обновления статуса бронирования")
    @Test
    public void updateBookingTest() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);

        assertThat(bookingDtoCreated.getStatus(), is(equalTo(BookingStatus.WAITING)));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);

        assertThat(bookingDtoUpdated.getStatus(), is(equalTo(REJECTED)));
    }

    @DisplayName("Тест выдачи исключения при попытке бронивания собственной вещи")
    @Test
    public void notCreateBookingIfBookerEqualsOwner() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto item = itemService.createItem(userDtoOne.getId(), itemDtoCreate);
        bookingDtoCreate.setItemId(item.getId());

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.createBooking(userDtoOne.getId(), bookingDtoCreate));
        assertEquals("Предмет с данным id = " + bookingDtoCreate.getItemId() + " не найден или не доступен", exception.getMessage());
    }

    @DisplayName("Тест выдачи исключения, если вещи не существует")
    @Test
    public void notCreateBookingIfItemNotExists() {
        userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        bookingDtoCreate.setItemId(2005L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.createBooking(userDtoTwo.getId(), bookingDtoCreate));
        assertEquals("Предмет с данным id = " + bookingDtoCreate.getItemId() + " не найден", exception.getMessage());
    }

    @DisplayName("Тест выдачи исключения, если вещь занята")
    @Test
    public void notCreateBookingIfTheItemIsNotAvailable() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoCreate);
        bookingDtoCreate.setItemId(itemDto.getId());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(userDtoTwo.getId(), bookingDtoCreate)
        );
        assertEquals("Предмет с данным id = " + bookingDtoCreate.getItemId()
                + " не найден или не доступен", exception.getMessage());
    }

    @DisplayName("Тест выдачи исключения, если статус до обновления не Waiting")
    @Test
    public void notUpdateBookingIfStatusNotWaiting() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);

        assertThat(BookingStatus.WAITING, is(bookingDtoCreated.getStatus()));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);

        assertThat(bookingDtoUpdated.getStatus(), is(equalTo(REJECTED)));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true)
        );
        assertEquals("Статус резерва не WAITING", exception.getMessage());
    }

    @DisplayName("Тест на выдачу исключения, при попытке обновления не существующего статуса бронирования")
    @Test
    public void notUpdateBookingNotExists() {
        long bookingId = 3225L;
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userDtoOne.getId(), bookingId, false)
        );
        assertEquals("Резевр с id = " + bookingId + " не найден", exception.getMessage());
    }

    @DisplayName("Тест на выдачу исключения, при попытке изменить статус не владельцем бронирования")
    @Test
    public void notUpdateBooking() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);

        assertThat(bookingDtoCreated.getStatus(), equalTo(BookingStatus.WAITING));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userDtoTwo.getId(), bookingDtoCreated.getId(), false)
        );
        assertEquals("id = " + bookingDtoCreated.getId() + " не найден", exception.getMessage());
    }

    @DisplayName("Тест на выдачу исключения, если бронирования не существует")
    @Test
    public void returnExceptionIfTheBookingDoesNotExist() {
        long bookingId = 500L;
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getOneBookingUser(bookingId, userDtoTwo.getId())
        );
        assertEquals("Id резерва = " + bookingId + " не найден", exception.getMessage());
    }

    @DisplayName("Возврат бронирования пользователю по id")
    @Test
    public void getOneBookingUser() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        BookingDto result = bookingService.getOneBookingUser(bookingDtoCreated.getId(), userDtoTwo.getId());

        assertThat(result).isEqualTo(bookingDtoCreated);
    }

    @DisplayName("Должен показать все бронирования пользователем")
    @Test
    public void getAllBookingsBooker() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        Collection<BookingDto> result = bookingService
                .getAllBookingBooker(userDtoTwo.getId(), BookingState.ALL, 0, 2);

        assertThat(result, contains(bookingDtoCreated));
    }

    @DisplayName("Должен показать владельцу все его бронирования вещей")
    @Test
    public void getAllBookingsOwner() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        Collection<BookingDto> result = bookingService
                .getAllBookingOwner(userDtoOne.getId(), BookingState.ALL, 0, 2);

        assertThat(result, contains(bookingDtoCreated));
    }

    @DisplayName("Тест на показ всех бронирований пользователя, со state = WAITING и REJECTED")
    @Test
    public void getAllBookingsBookerWaitingAndRejected() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        itemDtoCreate.setAvailable(true);
        ItemDto item = itemService.createItem(userDtoOne.getId(), itemDtoCreate);

        bookingDtoTwoCreate.setItemId(item.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        Collection<BookingDto> resultWaiting = bookingService.getAllBookingBooker(userDtoTwo.getId(), WAITING, 0, 2);
        assertThat(resultWaiting, contains(bookingDtoCreated));

        BookingDto bookingDtoUpdated = bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);
        Collection<BookingDto> resultRejected = bookingService.getAllBookingBooker(userDtoTwo.getId(), BookingState.REJECTED, 0, 2);
        assertThat(resultRejected, contains(bookingDtoUpdated));
    }

    @DisplayName("Тест на показ всех бронирований пользователя, со state = PAST, FUTURE и CURRENT")
    @Test
    public void getAllBookingsBookerCurrentAndFutureAndPast() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        bookingDtoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreatedPast = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        BookingDto bookingDtoCreatedFuture = bookingService.createBooking(userDtoTwo.getId(), bookingDtoCreate);

        Collection<BookingDto> resultPast = bookingService
                .getAllBookingBooker(userDtoTwo.getId(), BookingState.PAST, 0, 2);
        Collection<BookingDto> resultFuture = bookingService
                .getAllBookingBooker(userDtoTwo.getId(), BookingState.FUTURE, 0, 2);
        Collection<BookingDto> resultCurrent = bookingService
                .getAllBookingBooker(userDtoTwo.getId(), BookingState.CURRENT, 0, 2);

        assertThat(resultCurrent, empty());
        assertThat(resultPast, contains(bookingDtoCreatedPast));
        assertThat(resultFuture, contains(bookingDtoCreatedFuture));
    }

    @DisplayName("Тест на показ всех бронирований владельца, со state = PAST, FUTURE и CURRENT")
    @Test
    public void getAllBookingsOwnerCurrentAndFutureAndPast() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        bookingDtoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreatedPast = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        BookingDto bookingDtoCreatedFuture = bookingService.createBooking(userDtoTwo.getId(), bookingDtoCreate);

        Collection<BookingDto> resultPast = bookingService
                .getAllBookingOwner(userDtoOne.getId(), BookingState.PAST, 0, 2);
        Collection<BookingDto> resultFuture = bookingService
                .getAllBookingOwner(userDtoOne.getId(), BookingState.FUTURE, 0, 2);
        Collection<BookingDto> resultCurrent = bookingService
                .getAllBookingOwner(userDtoOne.getId(), BookingState.CURRENT, 0, 2);

        assertThat(resultCurrent, empty());
        assertThat(resultPast, contains(bookingDtoCreatedPast));
        assertThat(resultFuture, contains(bookingDtoCreatedFuture));
    }

    @DisplayName("Тест на показ всех бронирований владельца, со state = WAITING и REJECTED")
    @Test
    public void getAllBookingsOwnerWaitingAndRejected() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(userDtoOne.getId(), itemDtoOneCreate);

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(userDtoTwo.getId(), bookingDtoTwoCreate);
        Collection<BookingDto> resultWaiting = bookingService
                .getAllBookingOwner(userDtoOne.getId(), WAITING, 0, 2);

        assertThat(resultWaiting, contains(bookingDtoCreated));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);
        Collection<BookingDto> resultRejected = bookingService
                .getAllBookingOwner(userDtoOne.getId(), BookingState.REJECTED, 0, 2);

        assertThat(resultRejected, contains(bookingDtoUpdated));
    }
}
