package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.dto.ItemWithBookingsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;


    @SneakyThrows
    @Test
    void create_shouldCreateItemAndReturn200() {
        ItemDto itemDtoToCreate = new ItemDto(
                null,
                "Laptop",
                "Gaming laptop",
                true,
                2L,
                null);

        ItemDto createdItemDto = new ItemDto(
                1L,
                "Laptop",
                "Gaming laptop",
                true,
                2L,
                null);


        when(itemService.create(1L, itemDtoToCreate)).thenReturn(createdItemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        assertEquals(objectMapper.writeValueAsString(createdItemDto), result);

    }

    @SneakyThrows
    @Test
    void update_shouldUpdateItemAndReturn200() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "Updated Laptop", "Red gaming laptop", true);
        ItemDto updatedItemDto = new ItemDto(
                1L,
                "Updated Laptop",
                "Red gaming laptop",
                true,
                2L,
                null);

        when(itemService.update(1L, itemUpdateDto, 1L)).thenReturn(updatedItemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(updatedItemDto), result);
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnItemAndReturn200() {
        CommentDto commentDto = new CommentDto(
                null,
                "Laptop is super!",
                2L,
                1L,
                "Ivan",
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0));

        ItemWithBookingsDto itemDto = new ItemWithBookingsDto(
                1L,
                "Laptop",
                "Gaming laptop",
                true,
                2L,
                null,
                null,
                List.of(commentDto));

        when(itemService.getById(1L, 1L)).thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }


    @SneakyThrows
    @Test
    void getAllItemsByOwnerWithBookings_shouldReturnItemsListAndReturn200() {
        List<ItemWithBookingsDto> items = List.of(new ItemWithBookingsDto(
                1L,
                "Laptop",
                "Gaming laptop",
                true,
                null,
                null,
                null,
                null));

        when(itemService.getAllItemsByOwnerWithBookings(1L)).thenReturn(items);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void getAllItemsByText_shouldReturnItemsListAndReturn200() {
        List<ItemDto> items = List.of(new ItemDto(
                null,
                "Laptop",
                "Gaming laptop",
                true,
                2L,
                null));

        when(itemService.getAllItemsByText("Laptop")).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "Laptop"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void createComment_shouldCreateCommentAndReturn200() {
        CommentDto commentDto = new CommentDto(
                null,
                "Laptop is super!",
                2L,
                1L,
                "Ivan",
                null);
        CommentDto createdCommentDto = new CommentDto(
                3L,
                "Laptop is super!",
                2L,
                1L,
                "Ivan",
                null);

        when(itemService.createComment(1L, 2L, commentDto)).thenReturn(createdCommentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 2L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdCommentDto), result);
    }

}