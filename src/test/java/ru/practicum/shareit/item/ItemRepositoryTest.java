package ru.practicum.shareit.item;


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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
        ownerTwo = userRepository.save(new User(null, "John", "john@mail.ru"));

        itemOne = itemRepository.save(new Item(null, "shovel", "sand shovel", true, ownerOne, null));
        itemTwo = itemRepository.save(new Item(null, "hammer", "wooden hammer", true, ownerTwo, null));
        itemThree = itemRepository.save(new Item(null, "saw", "metal saw", false, ownerTwo, null));
    }

    @DisplayName("Тест нахождения всех предметов пользователя по id")
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

    @DisplayName("Тест подтверждения или опровержения наличия по id вещи и владельца")
    @Test
    public void existsByIdAndOwner_Id() {
        boolean result1 = itemRepository.existsByIdAndOwner_Id(-1L, -1L);
        boolean result2 = itemRepository.existsByIdAndOwner_Id(itemOne.getId(), ownerOne.getId());

        assertFalse(result1);
        assertTrue(result2);
    }

    @DisplayName("Тест на нахождение свободного предмета в имени или описании")
    @Test
    public void findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase() {
        Pageable pageable = getPageable(0, 10);
        String text1 = "GDFF";
        String text2 = "dfgdr";
        String text3 = "shovel";

        List<Item> items1 = itemRepository.findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(text1, text1, pageable);
        List<Item> items2 = itemRepository.findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(text2, text2, pageable);
        List<Item> items3 = itemRepository.findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(text3, text3, pageable);

        assertThat(items1, hasSize(0));
        assertThat(items2, hasSize(0));
        assertThat(items3, hasSize(1));
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
