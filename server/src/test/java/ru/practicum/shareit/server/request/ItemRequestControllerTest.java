package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;


    @SneakyThrows
    @Test
    void create_shouldCreateRequestAndReturn200() {
        ItemRequestDto itemRequestDtoToCreate = new ItemRequestDto(
                null,
                "Request for laptop",
                LocalDateTime.of(2025, 1, 2, 12, 30, 0, 0));

        ItemRequestDto createdItemRequestDto = new ItemRequestDto(
                1L,
                "Request for laptop",
                LocalDateTime.of(2025, 1, 2, 12, 30, 0, 0));

        when(itemRequestService.create(1L, itemRequestDtoToCreate)).thenReturn(createdItemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdItemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getAllByRequestorIdWithSort_shouldReturnAllRequestsByRequestorIdAndReturn200() {
        long requesterId = 1L;
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk());
        verify(itemRequestService).getAllByRequestorIdWithSort(requesterId);
    }

    @SneakyThrows
    @Test
    void getAllWithSort_shouldReturnAllRequestsAndReturn200() {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk());
        verify(itemRequestService).getAllWithSort();
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnRequestAndReturn200() {
        long requestId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk());
        verify(itemRequestService).getById(requestId);
    }
}