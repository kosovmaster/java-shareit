package ru.practicum.shareit.booking;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.Constant.FIXED_TIME;
import static ru.practicum.shareit.booking.BookingStatus.*;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User bookerOne;
    private User bookerTwo;
    private User ownerOne;
    private User ownerTwo;
    private Booking bookingOne;
    private Booking bookingTwo;
    private Booking bookingThree;
    private Booking bookingFour;
    private Booking bookingFive;
    private Booking bookingSix;
    private Booking bookingSeven;
    private Booking bookingEight;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    public void setUp() {
        ownerOne = userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        ownerTwo = userRepository.save(new User(null, "Lisa", "lisa@mail.ru"));
        bookerOne = userRepository.save(new User(null, "Sofia", "sofia@mail.ru"));
        bookerTwo = userRepository.save(new User(null, "Nana", "nana@mail.ru"));

        itemOne = itemRepository.save(new Item(null, "saw", "wood saw", true, ownerOne, null));
        itemTwo = itemRepository.save(new Item(null, "rake", "leaf rake", true, ownerTwo, null));

        bookingOne = bookingRepository.save(new Booking(null, FIXED_TIME.minusHours(5), FIXED_TIME.plusDays(1), itemOne, bookerOne, APPROVED));
        bookingTwo = bookingRepository.save(new Booking(null, FIXED_TIME.minusDays(2), FIXED_TIME.minusDays(1), itemOne, bookerOne, REJECTED));
        bookingThree = bookingRepository.save(new Booking(null, FIXED_TIME.plusDays(2), FIXED_TIME.plusDays(4), itemOne, bookerTwo, WAITING));
        bookingFour = bookingRepository.save(new Booking(null, FIXED_TIME, FIXED_TIME.plusDays(4), itemOne, bookerTwo, APPROVED));

        bookingFive = bookingRepository.save(new Booking(null, FIXED_TIME, FIXED_TIME.plusDays(3), itemTwo, bookerOne, WAITING));
        bookingSix = bookingRepository.save(new Booking(null, FIXED_TIME.minusHours(7), FIXED_TIME.plusDays(1), itemTwo, bookerOne, APPROVED));
        bookingSeven = bookingRepository.save(new Booking(null, FIXED_TIME.plusDays(2).plusHours(1), FIXED_TIME.plusDays(3), itemTwo, bookerTwo, REJECTED));
        bookingEight = bookingRepository.save(new Booking(null, FIXED_TIME.minusDays(6), FIXED_TIME.minusDays(4), itemTwo, bookerTwo, APPROVED));
    }

    @DisplayName("Тест нахождения всех бронирований по id владельца")
    @Test
    public void findAllByItem_Owner_Id() {
        Pageable pageable = getPageable();

        List<Booking> bookingListOne = bookingRepository.findAllByItem_Owner_Id(ownerOne.getId(), pageable);
        List<Booking> bookingListTwo = bookingRepository.findAllByItem_Owner_Id(ownerTwo.getId(), pageable);
        List<Booking> bookingListThree = bookingRepository.findAllByItem_Owner_Id(-1L, pageable);

        assertThat(bookingListOne, Matchers.containsInAnyOrder(bookingThree, bookingFour, bookingTwo, bookingOne));
        assertThat(bookingListTwo, Matchers.containsInAnyOrder(bookingFive, bookingEight, bookingSeven, bookingSix));
        assertThat(bookingListThree, hasSize(0));
    }

    @DisplayName("Тест нахождения всех бронирований по id владельца и статусу")
    @Test
    public void findAllByItem_Owner_IdAndStatus() {
        Pageable pageable = getPageable();

        List<Booking> bookingListOne = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerOne.getId(), APPROVED, pageable);
        List<Booking> bookingListTwo = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerTwo.getId(), REJECTED, pageable);
        List<Booking> bookingListThree = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerTwo.getId(), WAITING, pageable);
        List<Booking> bookingListFour = bookingRepository.findAllByItem_Owner_IdAndStatus(-1L, WAITING, pageable);

        assertThat(bookingListOne, Matchers.containsInAnyOrder(bookingFour, bookingOne));
        assertThat(bookingListTwo, Matchers.is(equalTo(List.of(bookingSeven))));
        assertThat(bookingListThree, Matchers.is(equalTo(List.of(bookingFive))));
        assertThat(bookingListFour, hasSize(0));
    }

    @DisplayName("Тест нахождения всех бронирований по id владельца и id пользователя")
    @Test
    public void findBookingByIdAndUser() {
        Booking bookingOne = bookingRepository.findBookingByIdAndUser(bookingEight.getId(), ownerTwo.getId()).orElse(null);
        Booking bookingTwo = bookingRepository.findBookingByIdAndUser(bookingEight.getId(), bookerTwo.getId()).orElse(null);
        Booking bookingThree = bookingRepository.findBookingByIdAndUser(bookingEight.getId(), ownerOne.getId()).orElse(null);

        assertNotNull(bookingOne);
        assertThat(bookingOne, Matchers.is(equalTo(bookingEight)));
        assertNotNull(bookingTwo);
        assertThat(bookingTwo, Matchers.is(equalTo(bookingEight)));
        assertNull(bookingThree);
    }

    @DisplayName("Тест нахождения всех бронирований по id пользователя, а также время окончания до текущего времени")
    @Test
    public void findAllByBooker_IdAndEndBefore() {
        Pageable pageable = getPageable();
        List<Booking> bookingListOne = bookingRepository.findAllByBooker_IdAndEndBefore(bookerOne.getId(), FIXED_TIME, pageable);
        List<Booking> bookingListTwo = bookingRepository.findAllByBooker_IdAndEndBefore(-1L, FIXED_TIME, pageable);

        assertThat(bookingListOne, Matchers.is(equalTo(List.of(bookingTwo))));
        assertThat(bookingListTwo, hasSize(0));
    }

    @DisplayName("Тест нахождения всех бронирований по id владельца, а также по статусу")
    @Test
    public void findAllByBooker_IdAndStatus() {
        Pageable pageable = getPageable();

        List<Booking> bookingListOne = bookingRepository.findAllByBooker_IdAndStatus(bookerOne.getId(), APPROVED, pageable);
        List<Booking> bookingListTwo = bookingRepository.findAllByBooker_IdAndStatus(bookerTwo.getId(), REJECTED, pageable);
        List<Booking> bookingListThree = bookingRepository.findAllByBooker_IdAndStatus(bookerTwo.getId(), WAITING, pageable);
        List<Booking> bookingListFour = bookingRepository.findAllByBooker_IdAndStatus(-1L, WAITING, pageable);

        assertThat(bookingListOne, Matchers.containsInAnyOrder(bookingSix, bookingOne));
        assertThat(bookingListTwo, Matchers.is(equalTo(List.of(bookingSeven))));
        assertThat(bookingListThree, Matchers.is(equalTo(List.of(bookingThree))));
        assertThat(bookingListFour, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id пользователя")
    @Test
    public void findAllByBooker_Id() {
        Pageable pageable = getPageable();

        List<Booking> bookingListOne = bookingRepository.findAllByBooker_Id(bookerOne.getId(), pageable);
        List<Booking> bookingListTwo = bookingRepository.findAllByBooker_Id(bookerTwo.getId(), pageable);
        List<Booking> bookingListThree = bookingRepository.findAllByBooker_Id(-1L, pageable);

        assertThat(bookingListOne, Matchers.containsInAnyOrder(bookingSix, bookingOne, bookingFive, bookingTwo));
        assertThat(bookingListTwo, Matchers.containsInAnyOrder(bookingEight, bookingSeven, bookingFour, bookingThree));
        assertThat(bookingListThree, hasSize(0));
    }

    @DisplayName("Поиск всех бронирований по id владельца, в текущий момент времени")
    @Test
    public void findAllByItem_Owner_IdAndStartBeforeAndEndAfter() {
        Pageable pageable = getPageable();

        List<Booking> result = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(ownerTwo.getId(), FIXED_TIME, FIXED_TIME, pageable);
        assertThat(result, Matchers.is(equalTo(List.of(bookingSix))));
    }

    @DisplayName("Поиск всех бронирований по id владельца, после текущего времени начала")
    @Test
    public void findAllByItem_Owner_IdAndStartAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByItem_Owner_IdAndStartAfter(ownerOne.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository.findAllByItem_Owner_IdAndStartAfter(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingThree))));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Тест поиска всех бронирований по id, до и после окончания текущего времени")
    @Test
    public void findAllByBooker_IdAndStartBeforeAndEndAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(bookerOne.getId(), FIXED_TIME, FIXED_TIME, pageable);
        assertThat(resultOne, Matchers.containsInAnyOrder(bookingOne, bookingSix));
    }

    @DisplayName("Тест поиска всех бронирований по id, после текущего времени")
    @Test
    public void findAllByBooker_IdAndStartAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByBooker_IdAndStartAfter(bookerTwo.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository.findAllByBooker_IdAndStartAfter(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.containsInAnyOrder(bookingSeven, bookingThree));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Поиск всех бронирований по id владельца и времени окончания до текущего времени")
    @Test
    public void findAllByItem_Owner_IdAndEndBefore() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByItem_Owner_IdAndEndBefore(ownerOne.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository.findAllByItem_Owner_IdAndEndBefore(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingTwo))));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Поиск следующего бронирования для владельца")
    @Test
    public void findNextBookingsForOwner() {
        List<Booking> bookings = bookingRepository.findNextBookingsForOwner(FIXED_TIME, List.of(itemOne.getId()), APPROVED);
        assertThat(bookings, Matchers.is(equalTo(List.of(bookingFour))));
    }

    @DisplayName("Поиск последнего бронирования для владельца")
    @Test
    public void findLastBookingsForOwner() {
        List<Booking> bookings = bookingRepository.findLastBookingsForOwner(FIXED_TIME, List.of(itemTwo.getId()), APPROVED);
        assertThat(bookings, hasSize(0));
    }

    @DisplayName("Тест проверки существование предмета по id и id бронирования, а также статусу до и после окончания текущего времени")
    @Test
    public void existsByItemIdAndBookerIdAndStatusAndEndBefore() {
        boolean resultOne = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemOne.getId(), bookerOne.getId(), REJECTED, FIXED_TIME);
        boolean resultTwo = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemOne.getId(), bookerOne.getId(), APPROVED, FIXED_TIME);
        assertTrue(resultOne);
        assertFalse(resultTwo);
    }

    @AfterEach
    public void deleteAll() {
        bookingRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private Pageable getPageable() {
        return PageRequest.of(0 / 10, 10, Sort.by(Sort.Order.desc("start")));
    }
}
