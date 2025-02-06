package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private Long bookerId;
    private Status status;

}
