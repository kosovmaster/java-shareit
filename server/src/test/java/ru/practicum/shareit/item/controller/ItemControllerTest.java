package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.ItemController;
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

    @DisplayName("Должен вернуть Item по id")
    @Test
    @SneakyThrows
    public void shouldGetItemById() {
        ItemDtoInfo itemDtoInfo = getItemDtoInfo();

        when(itemService.getItemDtoById(anyLong(), anyLong())).thenReturn(itemDtoInfo);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header(HEADER_USER, 1))
                .andExpect(jsonPath("$.id").value(itemDtoInfo.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoInfo.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoInfo.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoInfo.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(itemDtoInfo.getLastBooking().getId()))
                .andExpect(jsonPath("$.lastBooking.bookerId")
                        .value(itemDtoInfo.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$.lastBooking.start")
                        .value(itemDtoInfo.getLastBooking().getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.lastBooking.end")
                        .value(itemDtoInfo.getLastBooking().getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.lastBooking.status")
                        .value(String.valueOf(itemDtoInfo.getLastBooking().getStatus())))
                .andExpect(jsonPath("$.lastBooking.itemId")
                        .value(itemDtoInfo.getLastBooking().getItemId()))
                .andExpect(jsonPath("$.nextBooking.id")
                        .value(itemDtoInfo.getNextBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.bookerId")
                        .value(itemDtoInfo.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$.nextBooking.start")
                        .value(itemDtoInfo.getNextBooking().getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.nextBooking.end")
                        .value(itemDtoInfo.getNextBooking().getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.nextBooking.status")
                        .value(String.valueOf(itemDtoInfo.getNextBooking().getStatus())))
                .andExpect(jsonPath("$.nextBooking.itemId")
                        .value(itemDtoInfo.getNextBooking().getItemId()))
                .andExpect(jsonPath("$.comments[0].id")
                        .value(itemDtoInfo.getComments().get(0).getId()))
                .andExpect(jsonPath("$.comments[0].text")
                        .value(itemDtoInfo.getComments().get(0).getText()))
                .andExpect(jsonPath("$.comments[0].authorName")
                        .value(itemDtoInfo.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$.comments[0].created")
                        .value(itemDtoInfo.getComments().get(0).getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.comments[0].itemId")
                        .value(itemDtoInfo.getComments().get(0).getItemId()))
                .andExpect(status().isOk());

        verify(itemService).getItemDtoById(anyLong(), anyLong());
    }

    @DisplayName("Должен вернуть все Items владельцу")
    @Test
    @SneakyThrows
    public void shouldGetAllItemUser() {
        List<ItemDtoInfo> items = getItemDtoInfoList();

        when(itemService.getAllItemUser(anyLong(), anyInt(), anyInt())).thenReturn(items);

        mvc.perform(get("/items")
                        .header(HEADER_USER, 1))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(items.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(items.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].comments").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(items.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(items.get(1).getAvailable()))
                .andExpect(jsonPath("$[1].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[1].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[1].comments").isEmpty())
                .andExpect(status().isOk());

        verify(itemService).getAllItemUser(anyLong(), anyInt(), anyInt());
    }

    @DisplayName("Должен создать Item")
    @Test
    @SneakyThrows
    public void shouldCreateItem() {
        ItemDto itemDtoOneCreate = new ItemDto(null, "saw", "wood saw", true, null);
        ItemDto itemDto = new ItemDto(1L, "saw", "wood saw", true, null);

        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOneCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()))
                .andExpect(status().is(201));

        verify(itemService).createItem(any(), anyLong());
    }

    @DisplayName("Должен обновить Item")
    @Test
    @SneakyThrows
    public void shouldUpdateItem() {
        ItemDto itemDtoUpdate = new ItemDto(1L, "saw", "cool wood saw", true, null);

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDtoUpdate);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L))
                .andExpect(jsonPath("$.id").value(itemDtoUpdate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoUpdate.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoUpdate.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoUpdate.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDtoUpdate.getRequestId()))
                .andExpect(status().isOk());

        verify(itemService).updateItem(any(), anyLong(), anyLong());
    }

    @DisplayName("Должен найти Items по тексту в названии или описании")
    @Test
    @SneakyThrows
    public void shouldSearchItems() {
        String text = "garden";
        List<ItemDto> itemDtoList = List.of(
                new ItemDto(2L, "hoe", "garden hoe", true, 1L),
                new ItemDto(3L, "rake", "garden leaf rake", true, 2L)
        );

        when(itemService.searchItems(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(itemDtoList);

        mvc.perform(get("/items/search?text=", text, 10L, 2L)
                        .header(HEADER_USER, 1))
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

        verify(itemService).searchItems(anyString(), anyLong(), anyInt(), anyInt());
    }

    @DisplayName("Должен создать комментарий")
    @Test
    @SneakyThrows
    public void shouldCreateComment() {
        CommentDto commentDtoCreate = new CommentDto(null, "good", null, null, null);
        CommentDto commentDto = new CommentDto(1L, "good", "Ivan", FIXED_TIME, 1L);

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1))
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.itemId").value(commentDto.getItemId()))
                .andExpect(status().isOk());

        verify(itemService).createComment(any(), anyLong(), anyLong());
    }

    private ItemDtoInfo getItemDtoInfo() {
        return new ItemDtoInfo(1L, "saw", "wood saw", true,
                new BookingDtoInfo(1L, 1L,
                        FIXED_TIME.minusDays(2), FIXED_TIME.minusDays(1), APPROVED, 1L),
                new BookingDtoInfo(2L, 1L,
                        FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2), APPROVED, 1L),
                List.of(
                        new CommentDto(2L, "cool", "Maria", FIXED_TIME, 1L),
                        new CommentDto(3L, "ok", "Sveta", FIXED_TIME, 1L)
                )
        );
    }

    private List<ItemDtoInfo> getItemDtoInfoList() {
        return List.of(
                new ItemDtoInfo(1L, "hoe", "garden hoe", true,
                        new BookingDtoInfo(1L, 1L, FIXED_TIME.minusDays(2), FIXED_TIME.minusDays(1),
                                APPROVED, 1L),
                        new BookingDtoInfo(2L, 1L, FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2),
                                APPROVED, 1L),
                        List.of(
                                new CommentDto(2L, "cool", "Maria", FIXED_TIME, 1L),
                                new CommentDto(3L, "ok", "Sveta", FIXED_TIME, 1L)
                        )),
                new ItemDtoInfo(2L, "rake", "leaf rake", true,
                        new BookingDtoInfo(3L, 1L, FIXED_TIME.minusDays(3), FIXED_TIME.minusDays(1),
                                APPROVED, 2L
                        ),
                        new BookingDtoInfo(4L, 1L, FIXED_TIME.plusDays(1), FIXED_TIME.plusDays(2),
                                APPROVED, 2L
                        ),
                        new ArrayList<>())
        );
    }
}