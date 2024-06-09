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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_FORMAT;
import static ru.practicum.shareit.Constant.FIXED_TIME;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @DisplayName("Тест на корректную сериализацию объекта BookingDto")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        BookingDto bookingDto = BookingDto.builder()
                .start(FIXED_TIME)
                .end(FIXED_TIME.plusDays(3))
                .status(WAITING)
                .booker(new UserDto(null, "Inna", "inna@mail.ru"))
                .item(new ItemDto(null, "hoe", "garden hoe", true, null))
                .build();

        JsonContent<BookingDto> bookingDtoJson = this.json.write(bookingDto);

        assertThat(bookingDtoJson).hasJsonPathValue("$.start");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(FIXED_TIME.format(DATE_FORMAT));

        assertThat(bookingDtoJson).hasJsonPathValue("$.end");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(FIXED_TIME.plusDays(3).format(DATE_FORMAT));

        assertThat(bookingDtoJson).hasJsonPathValue("$.status");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.status")
                .isEqualTo(String.valueOf(WAITING));

        assertThat(bookingDtoJson).hasJsonPathValue("$.booker");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("Inna");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("inna@mail.ru");

        assertThat(bookingDtoJson).hasJsonPathValue("$.item");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.item.name").isEqualTo("hoe");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("garden hoe");
        assertThat(bookingDtoJson).extractingJsonPathValue("$.item.available").isEqualTo(true);
    }

    @DisplayName("Тест на корректную десериализацию объекта BookingDto")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        BookingDto bookingDto = new BookingDto(1L, FIXED_TIME, FIXED_TIME.plusDays(3), WAITING,
                new UserDto(null, "Inna", "inna@mail.ru"),
                new ItemDto(null, "hoe", "garden hoe", true, null));

        var resource = new ClassPathResource("bookingDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(bookingDto);
    }
}