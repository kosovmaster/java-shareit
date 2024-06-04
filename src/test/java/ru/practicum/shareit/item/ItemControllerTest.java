package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.validator.BookingDtoInfo;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constant.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    private ItemDtoInfo itemDtoInfo;

    @DisplayName("Тест создания предмета")
    @Test
    @SneakyThrows
    public void createItem() {
        ItemDto itemDto1 = new ItemDto(null, "shovel", "sand shovel", true, null);
        ItemDto itemDto2 = new ItemDto(1L, "shovel", "sand shovel", true, null);

        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto2);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$.id").value(itemDto2.getId()))
                .andExpect(jsonPath("$.name").value(itemDto2.getName()))
                .andExpect(jsonPath("$.description").value(itemDto2.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto2.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto2.getRequestId()))
                .andExpect(status().is(201));
        verify(itemService).createItem(anyLong(), any());
    }

    @DisplayName("Тест на возврат обновленных данных предмета")
    @Test
    @SneakyThrows
    public void updateItem() {
        ItemDto itemDto = new ItemDto(1L, "shovel", "sand shovel", true, null);

        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()))
                .andExpect(status().isOk());
        verify(itemService).updateItem(anyLong(), anyLong(), any());
    }

    @DisplayName("Тест на поиск всех преметов пользователя")
    @Test
    @SneakyThrows
    public void findAllItemUser() {
        List<ItemDtoInfo> items = getItemDtoInfoList();

        when(itemService.getAllItemUser(anyLong(), anyInt(), anyInt())).thenReturn(items);
        mvc.perform(get("/items")
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(items.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(items.get(0).isAvailable()))
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].comments").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(items.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(items.get(1).isAvailable()))
                .andExpect(jsonPath("$[1].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[1].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[1].comments").isEmpty())
                .andExpect(status().isOk());
        verify(itemService).getAllItemUser(anyLong(), anyInt(), anyInt());
    }

    @DisplayName("Тест поиска вещи по названию")
    @Test
    @SneakyThrows
    public void searchItem() {
        String text = "roof";
        List<ItemDto> itemDtoList = List.of(new ItemDto(2L, "hammer", "wooden hammer", true, 1L),
                new ItemDto(3L, "shovel", "sand shovel", true, 2L));

        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemDtoList);
        mvc.perform(get("/items/search?text=", 10L, 2L))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(itemDtoList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoList.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(itemDtoList.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDtoList.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].requestId").value(itemDtoList.get(0).getRequestId()))
                .andExpect(jsonPath("$[1].id").value(itemDtoList.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(itemDtoList.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(itemDtoList.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(itemDtoList.get(1).getAvailable()))
                .andExpect(jsonPath("$[1].requestId").value(itemDtoList.get(1).getRequestId()))
                .andExpect(status().isOk());
        verify(itemService).searchItem(anyString(), anyInt(), anyInt());
    }

    @DisplayName("Тест поиска предмета по id")
    @Test
    @SneakyThrows
    public void findById() {
        ItemDtoInfo itemDtoInfoResult = getItemDtoInfo();

        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(itemDtoInfoResult);
        mvc.perform(get("/items/{itemId}", 1L)
                        .header(USER_HEADER, 1))
                .andExpect(jsonPath("$.id").value(itemDtoInfoResult.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoInfoResult.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoInfoResult.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoInfoResult.isAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(itemDtoInfoResult.getLastBooking().getId()))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(itemDtoInfoResult.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$.lastBooking.start").value(itemDtoInfoResult.getLastBooking().getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.lastBooking.end").value(itemDtoInfoResult.getLastBooking().getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.lastBooking.status").value(String.valueOf(itemDtoInfoResult.getLastBooking().getStatus())))
                .andExpect(jsonPath("$.lastBooking.itemId").value(itemDtoInfoResult.getLastBooking().getItemId()))
                .andExpect(jsonPath("$.nextBooking.id").value(itemDtoInfoResult.getNextBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.bookerId").value(itemDtoInfoResult.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$.nextBooking.start").value(itemDtoInfoResult.getNextBooking().getStart().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.nextBooking.end").value(itemDtoInfoResult.getNextBooking().getEnd().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.nextBooking.status").value(String.valueOf(itemDtoInfoResult.getNextBooking().getStatus())))
                .andExpect(jsonPath("$.nextBooking.itemId").value(itemDtoInfoResult.getNextBooking().getItemId()))
                .andExpect(jsonPath("$.comments[0].id").value(itemDtoInfoResult.getComments().get(0).getId()))
                .andExpect(jsonPath("$.comments[0].text").value(itemDtoInfoResult.getComments().get(0).getText()))
                .andExpect(jsonPath("$.comments[0].authorName").value(itemDtoInfoResult.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$.comments[0].created").value(itemDtoInfoResult.getComments().get(0).getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.comments[0].itemId").value(itemDtoInfoResult.getComments().get(0).getItemId()))
                .andExpect(status().isOk());
        verify(itemService).findItemById(anyLong(), anyLong());
    }

    @DisplayName("Тест создания запроса")
    @Test
    @SneakyThrows
    public void createComment() {
        CommentDto commentDto1 = new CommentDto(null, "best", null, null, null);
        CommentDto commentDto2 = new CommentDto(1L, "best", "Ivan", FIXED_TIME, 1L);

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDto2);
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1))
                .andExpect(jsonPath("$.id").value(commentDto2.getId()))
                .andExpect(jsonPath("$.text").value(commentDto2.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto2.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto2.getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.itemId").value(commentDto2.getItemId()))
                .andExpect(status().isOk());
        verify(itemService).createComment(any(), anyLong(), anyLong());
    }

    private ItemDtoInfo getItemDtoInfo() {
        return new ItemDtoInfo(1L, "shovel", "sand shovel", true, new BookingDtoInfo(1L, 1L, FIXED_TIME.minusDays(2), FIXED_TIME.minusDays(1), APPROVED, 1L),
                new BookingDtoInfo(2L, 1L, FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2), APPROVED, 1L), List.of(
                new CommentDto(2L, "best", "Ivan", FIXED_TIME, 1L),
                new CommentDto(3L, "ok", "John", FIXED_TIME, 1L)
        ));
    }

    private List<ItemDtoInfo> getItemDtoInfoList() {
        return List.of(
                new ItemDtoInfo(1L, "hammer", "wooden hammer", true,
                        new BookingDtoInfo(1L, 1L, FIXED_TIME.minusDays(2), FIXED_TIME.minusDays(1), APPROVED, 1L),
                        new BookingDtoInfo(2L, 1L, FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2), APPROVED, 1L),
                        List.of(
                                new CommentDto(2L, "best", "Ivan", FIXED_TIME, 1L),
                                new CommentDto(3L, "normal", "John", FIXED_TIME, 1L)
                        )),
                new ItemDtoInfo(2L, "shovel", "sand shovel", true,
                        new BookingDtoInfo(3L, 1L, FIXED_TIME.minusDays(3), FIXED_TIME.minusDays(1), APPROVED, 2L),
                        new BookingDtoInfo(4L, 1L, FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2), APPROVED, 2L),
                        new ArrayList<>()));
    }
}
