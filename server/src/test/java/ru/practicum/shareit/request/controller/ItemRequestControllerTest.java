package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constant.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    private ItemRequestDtoInfo itemRequestDtoInfo;
    private ItemRequestDtoInfo itemRequestDtoInfoTwo;
    private ItemRequestDto itemRequestDtoCreateOne;
    private List<ItemRequestDtoInfo> itemRequestDtoInfoList;

    @BeforeEach
    public void setUp() {
        ItemDto itemDto = new ItemDto(1L, "saw", "wood saw", true, null);
        ItemDto itemDtoTwo = new ItemDto(2L, "rake", "leaf rake", true, null);
        itemRequestDtoInfo = new ItemRequestDtoInfo(1L, "need a saw", FIXED_TIME,
                Collections.singleton(itemDto));
        itemRequestDtoInfoTwo = new ItemRequestDtoInfo(2L, "need a garden hoe", FIXED_TIME,
                Collections.emptyList());
        itemRequestDtoCreateOne = new ItemRequestDto("need a saw");
        itemRequestDtoInfoList = List.of(
                new ItemRequestDtoInfo(1L, "need a saw", FIXED_TIME, Collections.singleton(itemDto)),
                new ItemRequestDtoInfo(2L, "need a rake", FIXED_TIME, Collections.singleton(itemDtoTwo)
                ));
    }

    @DisplayName("Должен создать запрос")
    @Test
    @SneakyThrows
    public void shouldCreateItemRequest() {
        ItemRequestDtoInfo request = itemRequestDtoInfo;

        when(itemRequestService.createItemRequest(any(), anyLong())).thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoCreateOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L))
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.created").value(request.getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.items[0].id").value(request.getItems().iterator().next().getId()))
                .andExpect(jsonPath("$.items[0].name").value(request.getItems().iterator().next().getName()))
                .andExpect(jsonPath("$.items[0].description")
                        .value(request.getItems().iterator().next().getDescription()))
                .andExpect(jsonPath("$.items[0].available")
                        .value(request.getItems().iterator().next().getAvailable()))
                .andExpect(jsonPath("$.items[0].requestId")
                        .value(request.getItems().iterator().next().getRequestId()))
                .andExpect(status().is(201));

        verify(itemRequestService).createItemRequest(any(), anyLong());
    }

    @DisplayName("Должен вернуть все запросы пользователя")
    @Test
    @SneakyThrows
    public void shouldGetListOfRequestsForItemsUser() {
        List<ItemRequestDtoInfo> request = List.of(itemRequestDtoInfoTwo);

        when(itemRequestService.getListOfRequestsForItemsUser(anyLong())).thenReturn(request);

        mvc.perform(get("/requests")
                        .header(HEADER_USER, 1L))
                .andExpect(jsonPath("$[0].id").value(request.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(request.get(0).getDescription()))
                .andExpect(jsonPath("$[0].created").value(request.get(0).getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(status().isOk());

        verify(itemRequestService).getListOfRequestsForItemsUser(anyLong());
    }

    @DisplayName("Должен вернуть запросы других пользователей постранично")
    @Test
    @SneakyThrows
    public void shouldGetItemRequestsPageByPage() {
        List<ItemRequestDtoInfo> requests = itemRequestDtoInfoList;

        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), anyLong())).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header(HEADER_USER, 1L))
                .andExpect(jsonPath("$[0].id").value(requests.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(requests.get(0).getDescription()))
                .andExpect(jsonPath("$[0].created").value(requests.get(0).getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[0].items").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(requests.get(1).getId()))
                .andExpect(jsonPath("$[1].description").value(requests.get(1).getDescription()))
                .andExpect(jsonPath("$[1].created").value(requests.get(1).getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[1].items").isNotEmpty())
                .andExpect(status().isOk());

        verify(itemRequestService).getAllItemRequests(anyInt(), anyInt(), anyLong());
    }

    @DisplayName("Должен вернуть запрос по id")
    @Test
    @SneakyThrows
    public void shouldGetItemRequestById() {
        ItemRequestDtoInfo request = itemRequestDtoInfo;

        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDtoInfo);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER_USER, 1L))
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.created").value(request.getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.items[0].id").value(request.getItems().iterator().next().getId()))
                .andExpect(jsonPath("$.items[0].name").value(request.getItems().iterator().next().getName()))
                .andExpect(jsonPath("$.items[0].description")
                        .value(request.getItems().iterator().next().getDescription()))
                .andExpect(jsonPath("$.items[0].available")
                        .value(request.getItems().iterator().next().getAvailable()))
                .andExpect(jsonPath("$.items[0].requestId")
                        .value(request.getItems().iterator().next().getRequestId()))
                .andExpect(status().isOk());

        verify(itemRequestService).getItemRequestById(anyLong(), anyLong());
    }
}