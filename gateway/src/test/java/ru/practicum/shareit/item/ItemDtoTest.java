package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Тест на корректную сериализацию объекта ItemDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemDto itemDto = ItemDto.builder()
                .name("hoe")
                .description("garden hoe")
                .available(true)
                .build();

        JsonContent<ItemDto> itemDtoJson = this.json.write(itemDto);

        assertThat(itemDtoJson).hasJsonPathValue("$.name");
        assertThat(itemDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("hoe");

        assertThat(itemDtoJson).hasJsonPathValue("$.description");
        assertThat(itemDtoJson).extractingJsonPathStringValue("$.description").isEqualTo("garden hoe");

        assertThat(itemDtoJson).hasJsonPathValue("$.available");
        assertThat(itemDtoJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @DisplayName("Тест на корректную десериализацию объекта ItemDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemDto itemDto = new ItemDto(null, "hoe", "garden hoe", true, null);

        var resource = new ClassPathResource("itemDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemDto);
    }

    @DisplayName("Проверка корректной валидации объекта ItemDto при создании и обновлении")
    @Test
    public void shouldValidation() {
        ItemDto itemDto = new ItemDto(0L, "", "", null, 0L);
        ItemDto itemDtoTwo = new ItemDto(-1L, "hoe", "garden hoe", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        Set<ConstraintViolation<ItemDto>> violationsTwo = validator.validate(itemDtoTwo, Update.class);

        assertThat(violations).isNotEmpty();
        assertThat(violationsTwo).isNotEmpty();
    }
}