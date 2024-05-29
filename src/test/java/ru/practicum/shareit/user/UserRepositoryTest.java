package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    protected void setUp() {
        userRepository.save(new User(null, "name", "name@mail.ru"));
    }

    @DisplayName("Подтверждение или опровержение существование в базе email")
    @Test
    public void existByEmail() {
        boolean result = userRepository.existsByEmail("name@mail.ru");
        assertTrue(result);
        boolean resultSecond = userRepository.existsByEmail("secondname@email.com");
        assertFalse(resultSecond);
    }

    @AfterEach
    protected void deleteUser() {
        userRepository.deleteAll();
    }
}
