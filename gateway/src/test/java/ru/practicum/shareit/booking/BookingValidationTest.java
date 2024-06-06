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

    @DisplayName("Должен выбросить исключение для владельца при неизвесном состоянии букинга")
    @Test
    @SneakyThrows
    public void returnExceptionOwnerForUnknownState() {
        mvc.perform(get("/bookings/owner?state=ERROR")
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Должен выбросить исключение для владельца при неизвесном состоянии букинга")
    @Test
    @SneakyThrows
    public void returnExceptionBookerForUnknownState() {
        mvc.perform(get("/bookings?state=ERROR")
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создавать бронирование, если id меньше 1")
    @Test
    @SneakyThrows
    public void notCreateBookingIfItemIdLessOne() {
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

    @DisplayName("Не должен создавать бронирование, если время окончание в прошлом времени")
    @Test
    @SneakyThrows
    public void notCreateBookingIfTimeEndInPast() {
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

    @DisplayName("Не должен создавать бронирование, если время начала в прошлом времени")
    @Test
    @SneakyThrows
    public void notCreateBookingIfTimeStartInPast() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                FIXED_TIME.minusDays(5), FIXED_TIME.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создавать бронирование, если время начала и конца равны")
    @Test
    @SneakyThrows
    public void notCreateBookingIfTimeStartAndTimeEndEquals() {
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

    @DisplayName("Не должен создавать бронирование, если начало и окончание равно null")
    @Test
    @SneakyThrows
    public void notCreateBookingIfTimeStartOrTimeEndEqualsNull() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L, null, null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Не должен создавать бронирование, если начальное время поздней конечного")
    @Test
    @SneakyThrows
    public void notCreateBookingIfTimeEndBeforeTimeStart() {
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
}
