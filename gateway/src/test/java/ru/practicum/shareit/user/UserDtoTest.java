package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Тест на корректную сериализацию объекта UserDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        UserDto userDto = UserDto.builder()
                .name("Inna")
                .email("inna@mail.ru")
                .build();

        JsonContent<UserDto> userDtoJson = this.json.write(userDto);

        assertThat(userDtoJson).hasJsonPathValue("$.name");
        assertThat(userDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("Inna");

        assertThat(userDtoJson).hasJsonPathValue("$.email");
        assertThat(userDtoJson).extractingJsonPathStringValue("$.email").isEqualTo("inna@mail.ru");
    }

    @DisplayName("Тест на корректную десериализацию объекта UserDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        UserDto userDto = new UserDto(null, "Inna", "inna@mail.ru");

        var resource = new ClassPathResource("userDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(userDto);
    }

    @DisplayName("Проверка корректной валидации объекта UserDto при создании и обновлении")
    @Test
    public void shouldValidation() {
        UserDto userDto = new UserDto(null, "", "");
        UserDto userDtoTwo = new UserDto(null, "Inna", "kjl");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        Set<ConstraintViolation<UserDto>> violationsTwo = validator.validate(userDtoTwo, Update.class);

        assertThat(violations).isNotEmpty();
        assertThat(violationsTwo).isNotEmpty();
    }
}