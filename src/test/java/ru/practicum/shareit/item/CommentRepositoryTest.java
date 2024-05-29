package ru.practicum.shareit.item;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
    BookingRepository bookingRepository;
    private Item itemFirst;
    private Item itemSecond;
    private Comment commentOne;
    private Comment commentTwo;
    private Comment commentThree;

    @BeforeEach
    public void setUp() {
        User owner = userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        User booker = userRepository.save(new User(null, "John", "john@mail.ru"));
        User bookerSecond = userRepository.save(new User(null, "Derek", "derek@mail.ru"));

        itemFirst = itemRepository.save(new Item(null, "shovel", "sand shovel", true, owner, null));
        itemSecond = itemRepository.save(new Item(null, "hammer", "wooden hammer", true, owner, null));

        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), itemFirst, booker, APPROVED));
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(4), itemSecond, booker, APPROVED));
        bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(5), itemSecond, bookerSecond, APPROVED));

        commentOne = commentRepository.save(new Comment(null, "Best", LocalDateTime.now(), itemFirst, booker));
        commentTwo = commentRepository.save(new Comment(null, "Normal", LocalDateTime.now(), itemSecond, booker));
        commentThree = commentRepository.save(new Comment(null, "Bad", LocalDateTime.now(), itemSecond, bookerSecond));
    }

    @DisplayName("Тест возврата всех комментариев по id вещи")
    @Test
    public void findAllByItemId() {
        List<Comment> resultOne = commentRepository.findAllByItemId(itemFirst.getId()).orElse(new ArrayList<>());
        List<Comment> resultTwo = commentRepository.findAllByItemId(itemSecond.getId()).orElse(new ArrayList<>());
        List<Comment> resultThree = commentRepository.findAllByItemId(-1L).orElse(new ArrayList<>());

        assertThat(resultOne, hasSize(1));
        assertThat(resultOne, is(equalTo(List.of(commentOne))));
        assertThat(resultTwo, hasSize(2));
        assertThat(resultTwo, is(equalTo(List.of(commentTwo, commentThree))));
        assertThat(resultThree, hasSize(0));
    }

    @DisplayName("Тест возврата всех комментариев для переданных id вещей")
    @Test
    public void findAllByItemIdIn() {
        List<Comment> resultOne = commentRepository.findAllByItemIdIn(List.of(itemSecond.getId(), itemFirst.getId())).orElse(new ArrayList<>());
        List<Comment> resultTwo = commentRepository.findAllByItemIdIn(List.of(itemSecond.getId())).orElse(new ArrayList<>());
        List<Comment> resultThree = commentRepository.findAllByItemIdIn(List.of(0L)).orElse(new ArrayList<>());

        assertThat(resultOne, hasSize(3));
        assertThat(resultOne, Matchers.is(equalTo(List.of(commentOne, commentTwo, commentThree))));
        assertThat(resultTwo, hasSize(2));
        assertThat(resultTwo, is(equalTo(List.of(commentTwo, commentThree))));
        assertThat(resultThree, hasSize(0));
    }

    @AfterEach
    public void deleteAll() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}
