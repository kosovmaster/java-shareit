package ru.practicum.shareit.booking;

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

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_TIME_FORMAT;
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
    public void serialize() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .bookerId(1L)
                .start(FIXED_TIME)
                .end(FIXED_TIME.plusDays(3))
                .status(WAITING)
                .itemId(1L)
                .build();

        JsonContent<BookingDtoInfo> bookingDtoInfoJsonContent = this.json.write(bookingDtoInfo);

        assertThat(bookingDtoInfoJsonContent).hasJsonPathValue("$.bookerId");
        assertThat(bookingDtoInfoJsonContent).extractingJsonPathValue("$.bookerId").isEqualTo(1);
        assertThat(bookingDtoInfoJsonContent).hasJsonPathValue("$.start");
        assertThat(bookingDtoInfoJsonContent).extractingJsonPathStringValue("$.start").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));
        assertThat(bookingDtoInfoJsonContent).hasJsonPathValue("$.end");
        assertThat(bookingDtoInfoJsonContent).extractingJsonPathStringValue("$.end").isEqualTo(FIXED_TIME.plusDays(3).format(DATE_TIME_FORMAT));
        assertThat(bookingDtoInfoJsonContent).hasJsonPathValue("$.status");
        assertThat(bookingDtoInfoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo(String.valueOf(WAITING));
        assertThat(bookingDtoInfoJsonContent).hasJsonPathValue("$.itemId");
        assertThat(bookingDtoInfoJsonContent).extractingJsonPathValue("$.itemId").isEqualTo(1);
    }

    @DisplayName("Тест на корректную десериализацию объекта BookingDtoInfo")
    @Test
    @SneakyThrows
    public void deserialize() {
        BookingDtoInfo bookingDtoInfo = new BookingDtoInfo(null, 1L, FIXED_TIME, FIXED_TIME.plusDays(3), WAITING, 1L);

        var resource = new ClassPathResource("bookingDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(bookingDtoInfo);
    }
}
