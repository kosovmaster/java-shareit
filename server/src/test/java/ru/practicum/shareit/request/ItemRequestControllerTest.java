package ru.practicum.shareit.request;

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
    private ItemRequestDtoInfo itemRequestDtoInfoFirst;
    private ItemRequestDtoInfo itemRequestDtoInfoSecond;
    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDtoInfo> itemRequestDtoInfoList;

    @BeforeEach
    public void setUp() {
        ItemDto itemDtoFirst = new ItemDto(1L, "shovel", "sand shovel", true, null);
        ItemDto itemDtoSecond = new ItemDto(2L, "hammer", "wooden hammer", true, null);
        itemRequestDtoInfoFirst = new ItemRequestDtoInfo(1L, "need shovel", FIXED_TIME, Collections.singleton(itemDtoFirst));
        itemRequestDtoInfoSecond = new ItemRequestDtoInfo(2L, "need hammer", FIXED_TIME, Collections.emptyList());
        itemRequestDto = new ItemRequestDto("need shovel");
        itemRequestDtoInfoList = List.of(
                new ItemRequestDtoInfo(1L, "need shovel", FIXED_TIME, Collections.singleton(itemDtoFirst)),
                new ItemRequestDtoInfo(2L, "need hammer", FIXED_TIME, Collections.singleton(itemDtoSecond)));
    }

    @DisplayName("Тест создания запроса")
    @Test
    @SneakyThrows
    public void shouldCreateItemRequest() {
        ItemRequestDtoInfo requestDtoInfo = itemRequestDtoInfoFirst;

        when(itemRequestService.createItemRequest(any(), anyLong())).thenReturn(requestDtoInfo);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$.id").value(requestDtoInfo.getId()))
                .andExpect(jsonPath("$.description").value(requestDtoInfo.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDtoInfo.getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.items[0].id").value(requestDtoInfo.getItems().iterator().next().getId()))
                .andExpect(jsonPath("$.items[0].name").value(requestDtoInfo.getItems().iterator().next().getName()))
                .andExpect(jsonPath("$.items[0].description").value(requestDtoInfo.getItems().iterator().next().getDescription()))
                .andExpect(jsonPath("$.items[0].available").value(requestDtoInfo.getItems().iterator().next().getAvailable()))
                .andExpect(jsonPath("$.items[0].requestId").value(requestDtoInfo.getItems().iterator().next().getRequestId()))
                .andExpect(status().is(201));
    }

    @DisplayName("Тест на возврат запроса по id")
    @Test
    @SneakyThrows
    public void shouldFindItemRequestById() {
        ItemRequestDtoInfo request = itemRequestDtoInfoFirst;

        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(request);
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.created").value(request.getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$.items[0].id").value(request.getItems().iterator().next().getId()))
                .andExpect(jsonPath("$.items[0].name").value(request.getItems().iterator().next().getName()))
                .andExpect(jsonPath("$.items[0].description").value(request.getItems().iterator().next().getDescription()))
                .andExpect(jsonPath("$.items[0].available").value(request.getItems().iterator().next().getAvailable()))
                .andExpect(jsonPath("$.items[0].requestId").value(request.getItems().iterator().next().getRequestId()))
                .andExpect(status().isOk());
        verify(itemRequestService).getItemRequestById(anyLong(), anyLong());
    }

    @DisplayName("Тест на возврат запросов пользователей постранично")
    @Test
    @SneakyThrows
    public void shouldGetItemRequestPageByPage() {
        List<ItemRequestDtoInfo> request = itemRequestDtoInfoList;

        when(itemRequestService.getItemRequestsPageByPage(anyInt(), anyInt(), anyLong())).thenReturn(request);
        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$[0].id").value(request.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(request.get(0).getDescription()))
                .andExpect(jsonPath("$[0].created").value(request.get(0).getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[0].items").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(request.get(1).getId()))
                .andExpect(jsonPath("$[1].description").value(request.get(1).getDescription()))
                .andExpect(jsonPath("$[1].created").value(request.get(1).getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[1].items").isNotEmpty())
                .andExpect(status().isOk());
    }

    @DisplayName("Тест возвращения всех запросов пользователя")
    @Test
    @SneakyThrows
    public void shouldGetListOfItemRequestsForItemUser() {
        List<ItemRequestDtoInfo> request = List.of(itemRequestDtoInfoSecond);

        when(itemRequestService.getListOfRequestsForItemUser(anyLong())).thenReturn(request);
        mvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(jsonPath("$[0].id").value(request.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(request.get(0).getDescription()))
                .andExpect(jsonPath("$[0].created").value(request.get(0).getCreated().format(DATE_TIME_FORMAT)))
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(status().isOk());
        verify(itemRequestService).getListOfRequestsForItemUser(anyLong());
    }
}
