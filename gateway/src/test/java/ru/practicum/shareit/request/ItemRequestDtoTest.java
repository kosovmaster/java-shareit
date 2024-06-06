package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Сериализация объекта ItemRequestDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("need shovel")
                .build();

        JsonContent<ItemRequestDto> itemRequestDtoJsonContent = this.json.write(itemRequestDto);

        assertThat(itemRequestDtoJsonContent).hasJsonPathValue("$.description");
        assertThat(itemRequestDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("need shovel");
    }

    @DisplayName("Десериализация объекта ItemRequestDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("need shovel");
        var resource = new ClassPathResource("itemRequestDto.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(itemRequestDto);
    }

    @DisplayName("Тест валидации объекта ItemRequestDto")
    @Test
    public void shouldValidate() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("");

        Set<ConstraintViolation<ItemRequestDto>> constraintViolations = validator.validate(itemRequestDto);

        assertThat(constraintViolations).isNotEmpty();
    }
}
