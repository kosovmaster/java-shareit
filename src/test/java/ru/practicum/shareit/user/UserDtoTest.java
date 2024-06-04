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

    @DisplayName("Сериализация объекта UserDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("ivan@email.com")
                .build();

        JsonContent<UserDto> userDtoJsonContent = this.json.write(userDto);

        assertThat(userDtoJsonContent).hasJsonPathValue("$.name");
        assertThat(userDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
        assertThat(userDtoJsonContent).hasJsonPathValue("$.email");
        assertThat(userDtoJsonContent).extractingJsonPathStringValue("$.email").isEqualTo("ivan@email.com");
    }

    @DisplayName("Десериализация объекта UserDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        UserDto userDto = new UserDto(null, "Ivan", "ivan@email.com");
        var resource = new ClassPathResource("userDto.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(userDto);
    }

    @DisplayName("Тест валидации объекта UserDto")
    @Test
    public void shouldValidate() {
        UserDto userDtoFirst = new UserDto(null, "", "");
        UserDto userDtoSecond = new UserDto(null, "Ivan", "com");

        Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDtoFirst, Create.class);
        Set<ConstraintViolation<UserDto>> constraintViolationSet = validator.validate(userDtoSecond, Create.class);

        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolationSet).isNotEmpty();
    }
}
