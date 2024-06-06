package ru.practicum.shareit.item.repository;

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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User ownerOne;
    private User ownerTwo;
    private Item itemOne;
    private Item itemTwo;
    private Item itemThree;

    @BeforeEach
    public void setUp() {
        ownerOne = userRepository.save(new User(null, "Ivan", "ivan@mail.ru"));
        ownerTwo = userRepository.save(new User(null, "Lisa", "lisa@mail.ru"));

        itemOne = itemRepository.save(new Item(null, "saw", "wood saw",
                true, ownerOne, null));
        itemTwo = itemRepository.save(new Item(null, "rake", "leaf rake",
                true, ownerTwo, null));
        itemThree = itemRepository.save(new Item(null, "hoe", "hoe",
                false, ownerTwo, null));
    }

    @DisplayName("Должен найти все предметы по id владельца")
    @Test
    public void findAllByOwnerId() {
        Pageable pageable = getPageable(0, 10);
        List<Item> result = itemRepository.findAllByOwnerId(ownerOne.getId(), pageable);
        List<Item> resultTwo = itemRepository.findAllByOwnerId(ownerTwo.getId(), pageable);
        List<Item> resultThree = itemRepository.findAllByOwnerId(-1L, pageable);

        assertThat(result, hasSize(1));
        assertThat(result, contains(itemOne));
        assertThat(resultTwo, hasSize(2));
        assertThat(resultTwo, Matchers.is(equalTo(List.of(itemTwo, itemThree))));
        assertThat(resultThree, hasSize(0));
    }

    @DisplayName("Должен подтвердить или опровергнуть наличие вещи по ее id и id владельца")
    @Test
    public void existsByIdAndOwner_Id() {
        boolean result = itemRepository.existsByIdAndOwner_Id(-1L, -1L);
        boolean resultTwo = itemRepository.existsByIdAndOwner_Id(itemOne.getId(), ownerOne.getId());

        assertFalse(result);
        assertTrue(resultTwo);
    }

    @DisplayName("Должен найти по тексту свободные предметы, в имени или описании которых он есть")
    @Test
    public void findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase() {
        Pageable pageable = getPageable(0, 10);
        String text = "RaK";
        String textTwo = "lkjhgf";
        String textThree = "hoe";

        List<Item> result = itemRepository
                .findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(
                        text, text, pageable);
        List<Item> resultTwo = itemRepository
                .findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(
                        textTwo, textTwo, pageable);
        List<Item> resultThree = itemRepository
                .findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(
                        textThree, textThree, pageable);

        assertThat(result, hasSize(1));
        assertThat(result, contains(itemTwo));
        assertThat(resultTwo, hasSize(0));
        assertThat(resultThree, hasSize(0));
    }

    @AfterEach
    public void deleteAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private Pageable getPageable(Integer from, Integer size) {
        return PageRequest.of(from / size, size, Sort.unsorted());
    }
}