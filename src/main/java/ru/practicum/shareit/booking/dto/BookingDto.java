package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class BookingDto {

    private Long id;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата начала должна быть сегодня или в будущем")
    private LocalDate start;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата окончания должна быть сегодня или в будущем")
    private LocalDate end;

    @NotNull(message = "Должен быть указан id арендуемой вещи.")
    private Long itemId;

    @NotNull(message = "Должен быть указан id арендатора.")
    private Long bookerId;

    @NotNull(message = "Должен быть указан статус")
    private Status status;

    @AssertTrue(message = "Дата окончания должна быть равна или позже даты начала")
    public Boolean isEndAfterStart() {
        return !start.isAfter(end); //могут быть равны
    }
}