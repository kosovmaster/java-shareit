package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class BookingDtoInfoTest {
    @Autowired
    private JacksonTester<BookingDtoInfo> json;

    @DisplayName("Тест на корректную сериализацию объекта BookingDtoInfo")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .bookerId(1L)
                .start(FIXED_TIME)
                .end(FIXED_TIME.plusDays(3))
                .status(WAITING)
                .itemId(1L)
                .build();

        JsonContent<BookingDtoInfo> bookingDtoInfoJson = this.json.write(bookingDtoInfo);

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.bookerId");
        assertThat(bookingDtoInfoJson).extractingJsonPathValue("$.bookerId").isEqualTo(1);

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.start");
        assertThat(bookingDtoInfoJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(FIXED_TIME.format(DATE_FORMAT));

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.end");
        assertThat(bookingDtoInfoJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(FIXED_TIME.plusDays(3).format(DATE_FORMAT));

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.status");
        assertThat(bookingDtoInfoJson).extractingJsonPathStringValue("$.status")
                .isEqualTo(String.valueOf(WAITING));

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.itemId");
        assertThat(bookingDtoInfoJson).extractingJsonPathValue("$.itemId").isEqualTo(1);
    }

    @DisplayName("Тест на корректную десериализацию объекта BookingDtoInfo")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        BookingDtoInfo bookingDtoInfo = new BookingDtoInfo(null, 1L, FIXED_TIME, FIXED_TIME.plusDays(3),
                WAITING, 1L);

        var resource = new ClassPathResource("bookingDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(bookingDtoInfo);
    }
}