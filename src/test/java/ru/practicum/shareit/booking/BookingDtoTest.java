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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.Constant.DATE_TIME_FORMAT;
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
    public void serialize() {
        BookingDto bookingDto = BookingDto.builder()
                .start(FIXED_TIME)
                .end(FIXED_TIME.plusDays(3))
                .status(WAITING)
                .booker(new UserDto(null, "Ivan", "ivan@mail.ru"))
                .item(new ItemDto(null, "hammer", "wooden hammer", true, null))
                .build();

        JsonContent<BookingDto> bookingDtoJsonContent = this.json.write(bookingDto);

        assertThat(bookingDtoJsonContent).hasJsonPathValue("$.start");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.start").isEqualTo(FIXED_TIME.format(DATE_TIME_FORMAT));
        assertThat(bookingDtoJsonContent).hasJsonPathValue("$.end");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.end").isEqualTo(FIXED_TIME.plusDays(3).format(DATE_TIME_FORMAT));
        assertThat(bookingDtoJsonContent).hasJsonPathValue("$.status");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo(String.valueOf(WAITING));
        assertThat(bookingDtoJsonContent).hasJsonPathValue("$.booker");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.booker.name").isEqualTo("Ivan");
        assertThat(bookingDtoJsonContent).hasJsonPathValue("$.booker");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.booker.email").isEqualTo("ivan@mail.ru");

        assertThat(bookingDtoJsonContent).hasJsonPathValue("$.item");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("hammer");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.item.description").isEqualTo("wooden hammer");
        assertThat(bookingDtoJsonContent).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @DisplayName("Тест на корректную десериализацию объекта BookingDto")
    @Test
    @SneakyThrows
    public void deserialize() {
        BookingDto bookingDto = new BookingDto(1L, FIXED_TIME, FIXED_TIME.plusDays(3),
                new UserDto(null, "Ivan", "ivan@mail.ru"),
                new ItemDto(null, "hammer", "wooden hammer", true, null), WAITING);

        var resource = new ClassPathResource("bookingDto.json");
        String content = Files.readString(resource.getFile().toPath());
        assertThat(this.json.parse(content)).isEqualTo(bookingDto);
    }
}
