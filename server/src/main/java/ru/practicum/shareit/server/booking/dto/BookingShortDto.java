package ru.practicum.shareit.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {

    private LocalDateTime start;

    private LocalDateTime end;
}
