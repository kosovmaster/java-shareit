package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constant.FIXED_TIME;
import static ru.practicum.shareit.Constant.USER_HEADER;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BookingValidationTest {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper mapper;

    @DisplayName("Не должен создать бронирование, если время окончания раньше времени начала")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeEndBeforeTimeStart() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                FIXED_TIME.plusDays(2), FIXED_TIME.plusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создать бронирование, если время начала или время окончания равно null")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeStartOrTimeEndEqualsNull() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L, null, null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создать бронирование, если время начала и время окончания совпадают")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeStartAndTimeEndEquals() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                FIXED_TIME.plusDays(2), FIXED_TIME.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создать бронирование, если время начала находится в прошлом")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeStartInPast() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                FIXED_TIME.minusDays(5), FIXED_TIME.plusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создать бронирование, если время окончания находится в прошлом")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeEndInPast() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                FIXED_TIME.minusDays(5), FIXED_TIME.minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создать бронирование, если id вещи меньше 1")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfItemIdLessOne() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(-5L,
                FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Должно выброситься исключение пользователю при неизвестном состоянии букинга")
    @Test
    @SneakyThrows
    public void shouldReturnExceptionBookerForUnknownState() {
        mvc.perform(get("/bookings?state=ERROR")
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Должно выброситься исключение владельцу вещи при неизвестном состоянии букинга")
    @Test
    @SneakyThrows
    public void shouldReturnExceptionOwnerForUnknownState() {
        mvc.perform(get("/bookings/owner?state=ERROR")
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }
}