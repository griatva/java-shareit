package ru.practicum.shareit.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingShortDto {

    private LocalDateTime bookingStart;

    private LocalDateTime bookingEnd;
}
