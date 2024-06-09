package ru.practicum.shareit.booking.repository;

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
import ru.practicum.shareit.booking.BookingRepository;
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
    private User ownerOne;
    private User ownerTwo;
    private User bookerOne;
    private User bookerTwo;
    private Item itemOne;
    private Item itemTwo;
    private Booking bookingOne;
    private Booking bookingTwo;
    private Booking bookingThree;
    private Booking bookingFour;
    private Booking bookingFive;
    private Booking bookingSix;
    private Booking bookingSeven;
    private Booking bookingEight;

    @BeforeEach
    public void setUp() {
        ownerOne = userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        ownerTwo = userRepository.save(new User(null, "Lisa", "lisa@mail.ru"));
        bookerOne = userRepository.save(new User(null, "Sofia", "sofia@mail.ru"));
        bookerTwo = userRepository.save(new User(null, "Nana", "nana@mail.ru"));

        itemOne = itemRepository.save(new Item(null, "saw", "wood saw",
                true, ownerOne, null));
        itemTwo = itemRepository.save(new Item(null, "rake", "leaf rake",
                true, ownerTwo, null));

        bookingOne = bookingRepository.save(new Booking(null,
                FIXED_TIME.minusHours(5), FIXED_TIME.plusDays(1), itemOne, bookerOne, APPROVED));
        bookingTwo = bookingRepository.save(new Booking(null,
                FIXED_TIME.minusDays(2), FIXED_TIME.minusDays(1), itemOne, bookerOne, REJECTED));
        bookingThree = bookingRepository.save(new Booking(null,
                FIXED_TIME.plusDays(2), FIXED_TIME.plusDays(4), itemOne, bookerTwo, WAITING));
        bookingFour = bookingRepository.save(new Booking(null,
                FIXED_TIME, FIXED_TIME.plusDays(4), itemOne, bookerTwo, APPROVED));

        bookingFive = bookingRepository.save(new Booking(null,
                FIXED_TIME, FIXED_TIME.plusDays(3), itemTwo, bookerOne, WAITING));
        bookingSix = bookingRepository.save(new Booking(null,
                FIXED_TIME.minusHours(7), FIXED_TIME.plusDays(1), itemTwo, bookerOne, APPROVED));
        bookingSeven = bookingRepository.save(new Booking(null,
                FIXED_TIME.plusDays(2).plusHours(1), FIXED_TIME.plusDays(3), itemTwo, bookerTwo, REJECTED));
        bookingEight = bookingRepository.save(new Booking(null,
                FIXED_TIME.minusDays(6), FIXED_TIME.minusDays(4), itemTwo, bookerTwo, APPROVED));
    }

    @DisplayName("Должен найти бронирование по его id и id пользователя")
    @Test
    public void findBookingByIdAndUser() {
        Booking resultOne = bookingRepository
                .findBookingByIdAndUser(bookingEight.getId(), ownerTwo.getId()).orElse(null);
        Booking resultTwo = bookingRepository
                .findBookingByIdAndUser(bookingEight.getId(), bookerTwo.getId()).orElse(null);
        Booking resultThree = bookingRepository
                .findBookingByIdAndUser(bookingEight.getId(), ownerOne.getId()).orElse(null);

        assertNotNull(resultOne);
        assertThat(resultOne, Matchers.is(equalTo(bookingEight)));
        assertNotNull(resultTwo);
        assertThat(resultTwo, Matchers.is(equalTo(bookingEight)));
        assertNull(resultThree);
    }

    @DisplayName("Должен найти все бронирования по id владельца")
    @Test
    public void findAllByItem_Owner_Id() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByItem_Owner_Id(ownerOne.getId(), pageable);
        List<Booking> resultTwo = bookingRepository.findAllByItem_Owner_Id(ownerTwo.getId(), pageable);
        List<Booking> resultThree = bookingRepository.findAllByItem_Owner_Id(-1L, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingThree, bookingFour, bookingOne, bookingTwo))));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(bookingSeven, bookingFive, bookingSix, bookingEight))));
        assertThat(resultThree, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id владельца и статусу")
    @Test
    public void findAllByItem_Owner_IdAndStatus() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByItem_Owner_IdAndStatus(ownerOne.getId(), APPROVED, pageable);
        List<Booking> resultTwo = bookingRepository
                .findAllByItem_Owner_IdAndStatus(ownerTwo.getId(), REJECTED, pageable);
        List<Booking> resultThree = bookingRepository
                .findAllByItem_Owner_IdAndStatus(ownerTwo.getId(), WAITING, pageable);
        List<Booking> resultFour = bookingRepository
                .findAllByItem_Owner_IdAndStatus(-1L, WAITING, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingFour, bookingOne))));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(bookingSeven))));
        assertThat(resultThree, Matchers.is(equalTo(List.of(bookingFive))));
        assertThat(resultFour, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id владельца и времени окончания до текущего времени")
    @Test
    public void findAllByItem_Owner_IdAndEndBefore() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByItem_Owner_IdAndEndBefore(ownerOne.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository
                .findAllByItem_Owner_IdAndEndBefore(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingTwo))));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id владельца и времени начала после текущего времени")
    @Test
    public void findAllByItem_Owner_IdAndStartAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByItem_Owner_IdAndStartAfter(ownerOne.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository
                .findAllByItem_Owner_IdAndStartAfter(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingThree))));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id владельца, которые попадают в текущий момент времени")
    @Test
    public void findAllByItem_Owner_IdAndStartBeforeAndEndAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByItem_Owner_IdAndStartBeforeAndEndAfter(ownerTwo.getId(), FIXED_TIME, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingSix))));
    }

    @DisplayName("Должен найти все бронирования по id пользователя, сделавшего это бронирование")
    @Test
    public void findAllByBooker_Id() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByBooker_Id(bookerOne.getId(), pageable);
        List<Booking> resultTwo = bookingRepository.findAllByBooker_Id(bookerTwo.getId(), pageable);
        List<Booking> resultThree = bookingRepository.findAllByBooker_Id(-1L, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingFive, bookingOne, bookingSix, bookingTwo))));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(bookingSeven, bookingThree, bookingFour, bookingEight))));
        assertThat(resultThree, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id пользователя, сделавшего это бронирование и статусу")
    @Test
    public void findAllByBooker_IdAndStatus() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository.findAllByBooker_IdAndStatus(bookerOne.getId(), APPROVED, pageable);
        List<Booking> resultTwo = bookingRepository.findAllByBooker_IdAndStatus(bookerTwo.getId(), REJECTED, pageable);
        List<Booking> resultThree = bookingRepository.findAllByBooker_IdAndStatus(bookerTwo.getId(), WAITING, pageable);
        List<Booking> resultFour = bookingRepository.findAllByBooker_IdAndStatus(-1L, WAITING, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingOne, bookingSix))));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(bookingSeven))));
        assertThat(resultThree, Matchers.is(equalTo(List.of(bookingThree))));
        assertThat(resultFour, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id пользователя, сделавшего это бронирование" +
            " и времени окончания до текущего времени")
    @Test
    public void findAllByBooker_IdAndEndBefore() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByBooker_IdAndEndBefore(bookerOne.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository.findAllByBooker_IdAndEndBefore(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingTwo))));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id пользователя сделавшего это бронирование" +
            " и времени начала после текущего времени")
    @Test
    public void findAllByBooker_IdAndStartAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByBooker_IdAndStartAfter(bookerTwo.getId(), FIXED_TIME, pageable);
        List<Booking> resultTwo = bookingRepository.findAllByBooker_IdAndStartAfter(-1L, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingSeven, bookingThree))));
        assertThat(resultTwo, hasSize(0));
    }

    @DisplayName("Должен найти все бронирования по id пользователя сделавшего это бронирование" +
            " и которые попадают в текущий момент времени")
    @Test
    public void findAllByBooker_IdAndStartBeforeAndEndAfter() {
        Pageable pageable = getPageable();

        List<Booking> resultOne = bookingRepository
                .findAllByBooker_IdAndStartBeforeAndEndAfter(bookerOne.getId(), FIXED_TIME, FIXED_TIME, pageable);

        assertThat(resultOne, Matchers.is(equalTo(List.of(bookingOne, bookingSix))));
    }

    @DisplayName("Должен найти следующее бронирование для владельца вещи")
    @Test
    public void findNextBookingsForOwner() {
        List<Booking> result = bookingRepository
                .findNextBookingsForOwner(FIXED_TIME, List.of(itemOne.getId()), APPROVED);

        assertThat(result, Matchers.is(equalTo(List.of(bookingFour))));
    }

    @DisplayName("Должен найти последнее бронирование для владельца вещи")
    @Test
    public void findLastBookingsForOwner() {
        List<Booking> result = bookingRepository
                .findLastBookingsForOwner(FIXED_TIME, List.of(itemTwo.getId()), APPROVED);

        assertThat(result, hasSize(0));
    }

    @DisplayName("Должен проверить существование бронирования по id вещи, " +
            "id пользователя, сделавшего это бронирование, статусу и времени окончания до текущего времени")
    @Test
    public void existsByItemIdAndBookerIdAndStatusAndEndBefore() {
        boolean result = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(itemOne.getId(), bookerOne.getId(),
                        REJECTED, FIXED_TIME);
        boolean resultTwo = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(itemOne.getId(), bookerOne.getId(),
                        APPROVED, FIXED_TIME);

        assertTrue(result);
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