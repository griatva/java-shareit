package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.validation.ValidStatus;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class BookingDto {

    private long id;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата начала должна быть сегодня или в будущем")
    private LocalDate start;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата окончания должна быть сегодня или в будущем")
    private LocalDate end;

    @NotNull(message = "Должен быть указан id арендуемой вещи.")
    private long itemId;

    @NotNull(message = "Должен быть указан id арендатора.")
    private long bookerId;

    @NotNull(message = "Должен быть указан статус")
    @ValidStatus(message = "Статус должен быть одним из: WAITING, APPROVED, REJECTED, CANCELED.")
    private Status status;

    @AssertTrue(message = "Дата окончания должна быть равна или позже даты начала")
    public boolean isEndAfterStart() {
        return !start.isAfter(end); //могут быть равны
    }
}