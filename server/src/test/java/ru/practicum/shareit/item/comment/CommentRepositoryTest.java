package ru.practicum.shareit.item.comment;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private Item itemOne;
    private Item itemTwo;
    private Comment commentOne;
    private Comment commentTwo;
    private Comment commentThree;

    @BeforeEach
    public void setUp() {
        User owner = userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        User booker = userRepository.save(new User(null, "Lisa", "lisa@mail.ru"));
        User bookerTwo = userRepository.save(new User(null, "Sveta", "sveta@mail.ru"));

        itemOne = itemRepository.save(new Item(null, "saw", "wood saw",
                true, owner, null));
        itemTwo = itemRepository.save(new Item(null, "rake", "leaf rake",
                true, owner, null));

        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                itemOne, booker, APPROVED));
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(4),
                itemTwo, booker, APPROVED));
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                itemTwo, bookerTwo, APPROVED));

        commentOne = commentRepository.save(new Comment(null, "cool", LocalDateTime.now(), itemOne, booker));
        commentTwo = commentRepository.save(new Comment(null, "bad", LocalDateTime.now(), itemTwo, booker));
        commentThree = commentRepository.save(new Comment(null, "ok", LocalDateTime.now(), itemTwo, bookerTwo));
    }

    @DisplayName("Должен вернуть все комментарии по id вещи")
    @Test
    public void findAllByItem_Id() {
        List<Comment> result = commentRepository.findAllByItem_Id(itemOne.getId()).orElse(new ArrayList<>());
        List<Comment> resultTwo = commentRepository.findAllByItem_Id(itemTwo.getId()).orElse(new ArrayList<>());
        List<Comment> resultThree = commentRepository.findAllByItem_Id(-1L).orElse(new ArrayList<>());

        assertThat(result, hasSize(1));
        assertThat(result, Matchers.is(equalTo(List.of(commentOne))));
        assertThat(resultTwo, hasSize(2));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(commentTwo, commentThree))));
        assertThat(resultThree, hasSize(0));
    }

    @DisplayName("Должен вернуть все комментарии для всех переданных id вещей")
    @Test
    public void findAllByItem_IdIn() {
        List<Comment> result = commentRepository
                .findAllByItem_IdIn(List.of(itemTwo.getId(), itemOne.getId())).orElse(new ArrayList<>());
        List<Comment> resultTwo = commentRepository
                .findAllByItem_IdIn(List.of(itemTwo.getId())).orElse(new ArrayList<>());
        List<Comment> resultThree = commentRepository.findAllByItem_IdIn(List.of(0L)).orElse(new ArrayList<>());

        assertThat(result, hasSize(3));
        assertThat(result, Matchers.is(equalTo(List.of(commentOne, commentTwo, commentThree))));
        assertThat(resultTwo, hasSize(2));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(commentTwo, commentThree))));
        assertThat(resultThree, hasSize(0));
    }

    @AfterEach
    public void deleteAll() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}