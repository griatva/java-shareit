package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата начала должна быть сегодня или в будущем")
    private LocalDateTime start;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата окончания должна быть сегодня или в будущем")
    private LocalDateTime end;

    @NotNull(message = "Должен быть указан id арендуемой вещи.")
    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private Status status;

    @AssertTrue(message = "Дата окончания должна быть равна или позже даты начала")
    public Boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}