package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_TIME_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class ItemDtoInfoTest {
    @Autowired
    private JacksonTester<ItemDtoInfo> json;

    @DisplayName("Тест сериализации для объекта ItemDtoInfo")
    @Test
    @SneakyThrows
    public void serialize() {
        ItemDtoInfo itemDtoInfo = ItemDtoInfo.builder()
                .name("Ivan")
                .description("wooden hammer")
                .available(true)
                .lastBooking(new BookingDtoInfo(null, 1L, FIXED_TIME, FIXED_TIME.plusDays(3), WAITING, 1L))
                .nextBooking(null)
                .comments(List.of(new CommentDto(null, "ok", "Ivan", FIXED_TIME, 1L)))
                .build();

        JsonContent<ItemDtoInfo> itemDtoInfoJsonContent = this.json.write(itemDtoInfo);

        assertThat(itemDtoInfoJsonContent).hasJsonPathValue("$.name");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
        assertThat(itemDtoInfoJsonContent).hasJsonPathValue("$.description");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("wooden hammer");
        assertThat(itemDtoInfoJsonContent).hasJsonPathValue("$.available");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(itemDtoInfoJsonContent).hasJsonPathValue("$.lastBooking");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.lastBooking.start").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.lastBooking.end").isEqualTo(FIXED_TIME.plusDays(3).format(DATE_TIME_FORMAT));
        assertThat(itemDtoInfoJsonContent).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo(String.valueOf(WAITING));
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.lastBooking.itemId").isEqualTo(1);

        assertThat(itemDtoInfoJsonContent).hasJsonPathValue("$.comments");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.comments[0].text").isEqualTo("ok");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.comments[0].authorName").isEqualTo("Ivan");
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.comments[0].created").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));
        assertThat(itemDtoInfoJsonContent).extractingJsonPathValue("$.comments[0].itemId").isEqualTo(1);
    }

    @DisplayName("Тест десериализации для объекта ItemDtoInfo")
    @Test
    @SneakyThrows
    public void deserialize() {
        ItemDtoInfo itemDtoInfo = new ItemDtoInfo(null, "hammer", "wooden hammer", true, null, new BookingDtoInfo(null, 1L, FIXED_TIME, FIXED_TIME.plusDays(3), WAITING, 1L),
                List.of(new CommentDto(null, "ok", "Ivan", FIXED_TIME, 1L)));
        var resource = new ClassPathResource("itemDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(itemDtoInfo);
    }
}
