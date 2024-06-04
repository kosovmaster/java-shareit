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
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_TIME_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;

@JsonTest
@AutoConfigureJsonTesters
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Сериализация объекта CommentDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        CommentDto commentDto = CommentDto.builder()
                .text("ok")
                .authorName("Ivan")
                .created(FIXED_TIME)
                .itemId(1L)
                .build();

        JsonContent<CommentDto> commentDtoJsonContent = this.json.write(commentDto);

        assertThat(commentDtoJsonContent).hasJsonPathValue("$.text");
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.text").isEqualTo("ok");

        assertThat(commentDtoJsonContent).hasJsonPathValue("$.authorName");
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("Ivan");

        assertThat(commentDtoJsonContent).hasJsonPathValue("$.created");
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.created").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));

        assertThat(commentDtoJsonContent).hasJsonPathValue("$.itemId");
        assertThat(commentDtoJsonContent).extractingJsonPathValue("$.itemId").isEqualTo(1);
    }

    @DisplayName("Десериализация объекта CommentDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        CommentDto commentDto = new CommentDto(null, "ok", "Ivan", FIXED_TIME, 1L);
        var resource = new ClassPathResource("commentDto.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(commentDto);
    }

    @DisplayName("Тест валидации объекта CommentDto")
    @Test
    public void shouldValidate() {
        CommentDto commentDto = new CommentDto(null, "", null, null, null);

        Set<ConstraintViolation<CommentDto>> constraintViolations = validator.validate(commentDto);

        assertThat(constraintViolations).isNotEmpty();
    }
}
