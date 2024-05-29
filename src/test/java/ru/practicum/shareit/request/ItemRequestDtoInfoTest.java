package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_TIME_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;

@JsonTest
@AutoConfigureJsonTesters
public class ItemRequestDtoInfoTest {
    @Autowired
    private JacksonTester<ItemRequestDtoInfo> json;

    @DisplayName("Тест сериализации для объекта ItemRequestDtoInfo")
    @Test
    @SneakyThrows
    public void serialize() {
        ItemRequestDtoInfo itemRequestDtoInfo = ItemRequestDtoInfo.builder()
                .description("need shovel")
                .created(FIXED_TIME)
                .items(List.of(new ItemDto(null, "shovel", "sand shovel", true, null)))
                .build();

        JsonContent<ItemRequestDtoInfo> itemRequestDtoInfoJsonContent = this.json.write(itemRequestDtoInfo);

        assertThat(itemRequestDtoInfoJsonContent).hasJsonPathValue("$.description");
        assertThat(itemRequestDtoInfoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("need shovel");
        assertThat(itemRequestDtoInfoJsonContent).hasJsonPathValue("$.created");
        assertThat(itemRequestDtoInfoJsonContent).extractingJsonPathStringValue("$.created").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));
        assertThat(itemRequestDtoInfoJsonContent).hasJsonPathValue("$.items");
        assertThat(itemRequestDtoInfoJsonContent).extractingJsonPathStringValue("$.items[0].name").isEqualTo("shovel");
        assertThat(itemRequestDtoInfoJsonContent).extractingJsonPathStringValue("$.items[0].description").isEqualTo("sand shovel");
        assertThat(itemRequestDtoInfoJsonContent).extractingJsonPathValue("$.items[0].available").isEqualTo(true);
    }

    @DisplayName("Тест десериализации для объекта ItemRequestDtoInfo")
    @Test
    @SneakyThrows
    public void deserialize() {
        ItemRequestDtoInfo itemRequestDtoInfo = new ItemRequestDtoInfo(null, "need shovel", FIXED_TIME, List.of(new ItemDto(null, "shovel", "sand shovel", true, null)));
        var resource = new ClassPathResource("ItemRequestDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(itemRequestDtoInfo);
    }
}
