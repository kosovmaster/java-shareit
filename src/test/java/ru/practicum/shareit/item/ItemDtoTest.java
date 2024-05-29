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

    @DisplayName("Сериализация объекта ItemDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemDto itemDto = ItemDto.builder()
                .name("shovel")
                .description("sand shovel")
                .available(true)
                .build();

        JsonContent<ItemDto> itemDtoJsonContent = this.json.write(itemDto);

        assertThat(itemDtoJsonContent).hasJsonPathValue("$.name");
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("shovel");

        assertThat(itemDtoJsonContent).hasJsonPathValue("$.description");
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("sand shovel");

        assertThat(itemDtoJsonContent).hasJsonPathValue("$.available");
        assertThat(itemDtoJsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @DisplayName("Десериализация объекта ItemDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemDto itemDto = new ItemDto(null, "shovel", "sand shovel", true, null);
        var resource = new ClassPathResource("itemDto.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(itemDto);
    }

    @DisplayName("Тест валидации объекта ItemDto")
    @Test
    public void shouldValidate() {
        ItemDto itemDto = new ItemDto(0L, "", "", null, 0L);
        ItemDto itemDto2 = new ItemDto(-1L, "shovel", "sand shovel", true, 1L);

        Set<ConstraintViolation<ItemDto>> constraintViolations = validator.validate(itemDto, Create.class);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto2, Update.class);

        assertThat(constraintViolations).isNotEmpty();
        assertThat(violations).isNotEmpty();
    }
}
