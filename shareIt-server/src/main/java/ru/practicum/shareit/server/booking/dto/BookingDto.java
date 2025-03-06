package ru.practicum.shareit.server.booking.dto;

import lombok.Data;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    private LocalDateTime bookingStart;

    private LocalDateTime bookingEnd;

    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private Status status;

}