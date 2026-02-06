package practicum.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import practicum.booking.enums.Status;
import practicum.item.dto.ItemDto;
import practicum.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    @NotNull(message = "Start date must not be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime start;

    @NotNull(message = "End date must not be null")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDateTime end;

    @NotNull(message = "Item id must be provided")
    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private Status status;

    @AssertTrue(message = "End date must be equal to or later than start date")
    public Boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}