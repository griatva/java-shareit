package ru.practicum.shareit.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.enums.BookingState;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;


    @SneakyThrows
    @Test
    void create_shouldCreateBookingAndReturn200() {
        BookingDto bookingDtoToCreate = new BookingDto(
                null,
                LocalDateTime.of(2025, 1, 2, 12, 30, 0, 0),
                LocalDateTime.of(2025, 2, 2, 12, 30, 0, 0),
                3L,
                new ItemDto(),
                new UserDto(),
                Status.WAITING);


        BookingDto createdBookingDto = new BookingDto(
                2L,
                LocalDateTime.of(2025, 1, 2, 12, 30, 0, 0),
                LocalDateTime.of(2025, 2, 2, 12, 30, 0, 0),
                3L,
                new ItemDto(),
                new UserDto(),
                Status.WAITING);

        when(bookingService.create(1L, bookingDtoToCreate)).thenReturn(createdBookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdBookingDto), result);
    }

    @SneakyThrows
    @Test
    void approveOrRejectBooking_shouldApproveOrRejectAndReturn200() {

        boolean approved = true;
        UserDto owner = new UserDto(7L, "Ivan Ivanov", "ivan@gmail.com");
        UserDto booker = new UserDto(1L, "Piotr Ivanov", "piotr@gmail.com");
        CommentDto commentDto = new CommentDto(
                3L,
                "Laptop is super!",
                2L,
                1L,
                "Ivan",
                LocalDateTime.of(2025, 2, 3, 12, 30, 0, 0));
        ItemDto itemDto = new ItemDto(
                3L,
                "Laptop",
                "Gaming laptop",
                true,
                2L,
                List.of(commentDto));

        BookingDto createdBookingDto = new BookingDto(
                2L,
                LocalDateTime.of(2025, 1, 2, 12, 30, 0, 0),
                LocalDateTime.of(2025, 2, 2, 12, 30, 0, 0),
                3L,
                itemDto,
                booker,
                Status.APPROVED);

        when(bookingService.approveOrRejectBooking(owner.getId(), createdBookingDto.getId(), approved)).thenReturn(createdBookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", createdBookingDto.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdBookingDto), result);
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnBookingAndReturn200() {
        long bookingId = 1L;
        long requesterId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk());

        verify(bookingService).getById(requesterId, bookingId);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerIdWithFilter_shouldReturnBookingsAndReturn200() {
        long bookerId = 1L;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk());

        verify(bookingService).getBookingsByBookerIdWithFilter(bookerId, BookingState.ALL);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerIdWithFilter_shouldReturnBookingsAndReturn200() {
        long ownerId = 1L;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk());

        verify(bookingService).getBookingsByOwnerIdWithFilter(ownerId, BookingState.ALL);
    }
}