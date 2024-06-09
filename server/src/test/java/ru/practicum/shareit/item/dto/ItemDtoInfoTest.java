package ru.practicum.shareit.item.dto;

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

import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class ItemDtoInfoTest {
    @Autowired
    private JacksonTester<ItemDtoInfo> json;

    @DisplayName("Тест на корректную сериализацию объекта ItemDtoInfo")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemDtoInfo itemDtoInfo = ItemDtoInfo.builder()
                .name("hoe")
                .description("garden hoe")
                .available(true)
                .lastBooking(new BookingDtoInfo(null, 1L, FIXED_TIME, FIXED_TIME.plusDays(3),
                        WAITING, 1L))
                .nextBooking(null)
                .comments(List.of(new CommentDto(null, "ok", "Sofia", FIXED_TIME, 1L)))
                .build();

        JsonContent<ItemDtoInfo> itemDtoInfoJson = this.json.write(itemDtoInfo);

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.name");
        assertThat(itemDtoInfoJson).extractingJsonPathStringValue("$.name").isEqualTo("hoe");

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.description");
        assertThat(itemDtoInfoJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("garden hoe");

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.available");
        assertThat(itemDtoInfoJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.lastBooking");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.start")
                .isEqualTo(FIXED_TIME.format(DATE_FORMAT));
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.end")
                .isEqualTo(FIXED_TIME.plusDays(3).format(DATE_FORMAT));
        assertThat(itemDtoInfoJson).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo(String.valueOf(WAITING));
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.itemId").isEqualTo(1);

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.comments");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].text").isEqualTo("ok");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].authorName")
                .isEqualTo("Sofia");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].created")
                .isEqualTo(FIXED_TIME.format(DATE_FORMAT));
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].itemId").isEqualTo(1);
    }

    @DisplayName("Тест на корректную десериализацию объекта ItemDtoInfo")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemDtoInfo itemDtoInfo = new ItemDtoInfo(null, "hoe", "garden hoe", true,
                new BookingDtoInfo(null, 1L, FIXED_TIME, FIXED_TIME.plusDays(3), WAITING, 1L),
                null, List.of(new CommentDto(null, "ok", "Sofia", FIXED_TIME, 1L)));

        var resource = new ClassPathResource("itemDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemDtoInfo);
    }
}