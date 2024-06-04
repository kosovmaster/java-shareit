package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.validator.BookingDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constant.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;

    @DisplayName("Тест создания бронирования")
    @Test
    @SneakyThrows
    public void shouldCreateBooking() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto bookingDto = getBookingDto();

        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.status").value(String.valueOf(bookingDto.getStatus())))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingDto.getItem().getRequestId()))
                .andExpect(status().is(201));
        verify(bookingService).createBooking(anyLong(), any());
    }

    @DisplayName("Тест обновления бронирования")
    @Test
    @SneakyThrows
    public void updateBookingTest() {
        BookingDto bookingDtoUpdate = getBookingDtoUpdate();

        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoUpdate);
        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                        .content(mapper.writeValueAsString(bookingDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 2L))
                .andExpect(jsonPath("$.id").value(bookingDtoUpdate.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoUpdate.getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.end").value(bookingDtoUpdate.getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.status").value(String.valueOf(bookingDtoUpdate.getStatus())))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoUpdate.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDtoUpdate.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDtoUpdate.getBooker().getEmail()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoUpdate.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDtoUpdate.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDtoUpdate.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDtoUpdate.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingDtoUpdate.getItem().getRequestId()))
                .andExpect(status().isOk());
        verify(bookingService).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @DisplayName("Тест возврат бронирования по id хозяину вещи или пользователю")
    @Test
    @SneakyThrows
    public void shouldGetOneBookingUser() {
        BookingDto bookingDto = getBookingDto();

        when(bookingService.getOneBookingUser(anyLong(), anyLong())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_HEADER, 2L))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.status").value(String.valueOf(bookingDto.getStatus())))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingDto.getItem().getRequestId()))
                .andExpect(status().isOk());
        verify(bookingService).getOneBookingUser(anyLong(), anyLong());
    }

    @DisplayName("Тест должен вернуть все бронирования пользователя")
    @Test
    @SneakyThrows
    public void shouldGetAllBookingsBooker() {
        List<BookingDto> bookings = List.of(getBookingDto());

        when(bookingService.getAllBookingBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);
        mvc.perform(get("/bookings?state=ALL")
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[0].status").value(String.valueOf(bookings.get(0).getStatus())))
                .andExpect(jsonPath("$[0].booker.id").value(bookings.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookings.get(0).getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookings.get(0).getBooker().getEmail()))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookings.get(0).getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookings.get(0).getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookings.get(0).getItem().getRequestId()))
                .andExpect(status().isOk());
        verify(bookingService).getAllBookingBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @DisplayName("Тест возврата все бронирования владельца")
    @Test
    @SneakyThrows
    public void shouldGetAllBookingsOwner() {
        List<BookingDto> bookings = getBookingDtoList();

        when(bookingService.getAllBookingOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);
        mvc.perform(get("/bookings/owner?state=ALL")
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[0].status").value(String.valueOf(bookings.get(0).getStatus())))
                .andExpect(jsonPath("$[0].booker.id").value(bookings.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookings.get(0).getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookings.get(0).getBooker().getEmail()))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookings.get(0).getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookings.get(0).getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookings.get(0).getItem().getRequestId()))
                .andExpect(status().isOk());
        verify(bookingService).getAllBookingOwner(anyLong(), any(), anyInt(), anyInt());
    }

    private BookingDto getBookingDto() {
        return new BookingDto(
                1L,
                FIXED_TIME.plusDays(1),
                FIXED_TIME.plusDays(2),
                new UserDto(1L, "Ivan", "ivan@mail.ru"),
                new ItemDto(1L, "shovel", "wooden shovel", true, null),
                WAITING);
    }

    private List<BookingDto> getBookingDtoList() {
        return List.of(
                new BookingDto(2L, FIXED_TIME.plusDays(3), FIXED_TIME.plusDays(4),
                        new UserDto(2L, "John", "john@mail.ru"),
                        new ItemDto(2L, "saw", "metal saw", true, null),
                        WAITING)
        );
    }

    private BookingDto getBookingDtoUpdate() {
        return new BookingDto(
                1L,
                FIXED_TIME.plusDays(1),
                FIXED_TIME.plusDays(2),
                new UserDto(1L, "Ivan", "ivan@mail.ru"),
                new ItemDto(1L, "shovel", "wooden shovel", true, null),
                APPROVED);
    }
}
