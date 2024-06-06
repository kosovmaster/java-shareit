package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    protected void setUp() {
        userRepository.save(new User(null, "Toma", "toma@mail.ru"));
    }

    @DisplayName("Должен подтвердить или опровергнуть существование в базе email")
    @Test
    public void existsByEmail() {
        boolean resultOne = userRepository.existsByEmail("toma@mail.ru");

        assertTrue(resultOne);

        boolean resultTwo = userRepository.existsByEmail("fffffkkkkddddd@mail.ru");

        assertFalse(resultTwo);
    }

    @AfterEach
    protected void deleteUsers() {
        userRepository.deleteAll();
    }
}