package ru.practicum.shareit.request.repository;

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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User requesterOne;
    private ItemRequest requestOne;
    private ItemRequest requestTwo;
    private ItemRequest requestThree;

    @BeforeEach
    public void setUp() {
        userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        requesterOne = userRepository.save(new User(null, "Lisa", "lisa@mail.ru"));
        User requesterTwo = userRepository.save(new User(null, "Marina", "marina@mail.ru"));

        requestOne = itemRequestRepository.save(new ItemRequest(null, "need a saw", requesterOne,
                LocalDateTime.now(), EMPTY_LIST));
        requestTwo = itemRequestRepository.save(new ItemRequest(null, "need a rake", requesterOne,
                LocalDateTime.now().minusDays(1), EMPTY_LIST));
        requestThree = itemRequestRepository.save(new ItemRequest(null, "need a hoe", requesterTwo,
                LocalDateTime.now(), EMPTY_LIST));
    }

    @DisplayName("Должен вернуть все запросы по id пользователя делавшего эти запросы " +
            "с сортировкой по времени создания запроса от более новых к старым")
    @Test
    public void findAllByRequester_IdOrderByCreatedDesc() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(requesterOne.getId());

        assertThat(result, hasSize(2));
        assertThat(result, Matchers.is(equalTo(List.of(requestOne, requestTwo))));
    }

    @DisplayName("Должен вернуть все запросы, кроме запросов самого пользователя")
    @Test
    public void findAllByRequester_IdNot() {
        Pageable pageable = getPageable(0, 10, "created");
        List<ItemRequest> result = itemRequestRepository.findAllByRequester_IdNot(requesterOne.getId(), pageable);

        assertThat(result, hasSize(1));
        assertThat(result, Matchers.is(equalTo(List.of(requestThree))));
    }

    @AfterEach
    public void deleteAll() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private Pageable getPageable(Integer from, Integer size, String sort) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Order.desc(sort)));
    }
}