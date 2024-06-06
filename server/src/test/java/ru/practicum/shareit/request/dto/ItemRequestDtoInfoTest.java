package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;

@JsonTest
@AutoConfigureJsonTesters
public class ItemRequestDtoInfoTest {
    @Autowired
    private JacksonTester<ItemRequestDtoInfo> json;

    @DisplayName("Тест на корректную сериализацию объекта ItemRequestDtoInfo")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemRequestDtoInfo itemRequestDtoInfo = ItemRequestDtoInfo
                .builder()
                .description("need hoe")
                .created(FIXED_TIME)
                .items(List.of(new ItemDto(null, "hoe", "garden hoe", true, null)))
                .build();

        JsonContent<ItemRequestDtoInfo> itemRequestDtoInfoJson = this.json.write(itemRequestDtoInfo);

        assertThat(itemRequestDtoInfoJson).hasJsonPathValue("$.description");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("need hoe");

        assertThat(itemRequestDtoInfoJson).hasJsonPathValue("$.created");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(FIXED_TIME.format(DATE_FORMAT));

        assertThat(itemRequestDtoInfoJson).hasJsonPathValue("$.items");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("hoe");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("garden hoe");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathValue("$.items[0].available")
                .isEqualTo(true);
    }

    @DisplayName("Тест на корректную десериализацию объекта ItemRequestDtoInfo")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemRequestDtoInfo itemRequestDtoInfo = new ItemRequestDtoInfo(null, "need hoe", FIXED_TIME,
                List.of(new ItemDto(null, "hoe", "garden hoe", true, null)));

        var resource = new ClassPathResource("itemRequestDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemRequestDtoInfo);
    }
}