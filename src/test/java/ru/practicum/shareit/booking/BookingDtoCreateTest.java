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
import ru.practicum.shareit.booking.validator.BookingDtoCreate;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_TIME_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;

@JsonTest
@AutoConfigureJsonTesters
public class BookingDtoCreateTest {
    @Autowired
    private JacksonTester<BookingDtoCreate> json;

    @DisplayName("Тест на корректную сериализацию объекта BookingDtoCreate")
    @Test
    @SneakyThrows
    public void serialize() {
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .itemId(1L)
                .start(FIXED_TIME)
                .end(FIXED_TIME.plusDays(3))
                .build();

        JsonContent<BookingDtoCreate> bookingDtoCreateJson = this.json.write(bookingDtoCreate);

        assertThat(bookingDtoCreateJson).hasJsonPathValue("$.itemId");
        assertThat(bookingDtoCreateJson).extractingJsonPathValue("$.itemId").isEqualTo(1);
        assertThat(bookingDtoCreateJson).hasJsonPathValue("$.start");
        assertThat(bookingDtoCreateJson).extractingJsonPathStringValue("$.start").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));
        assertThat(bookingDtoCreateJson).hasJsonPathValue("$.end");
        assertThat(bookingDtoCreateJson).extractingJsonPathStringValue("$.end").isEqualTo(FIXED_TIME.plusDays(3).format(DATE_TIME_FORMAT));
    }

    @DisplayName("Тест на корректную десериализацию объекта BookingDtoCreate")
    @Test
    @SneakyThrows
    public void deserialize() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L, FIXED_TIME, FIXED_TIME.plusDays(3));

        var resource = new ClassPathResource("bookingDtoCreate.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(bookingDtoCreate);
    }
}
