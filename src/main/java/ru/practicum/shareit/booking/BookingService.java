package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto create(Long bookerId, BookingDto bookingDto);

    BookingDto approveOrRejectBooking(Long ownerId, Long bookingId, Boolean isApproved);

    BookingDto getById(Long requesterId, Long bookingId);

    List<BookingDto> getBookingsByBookerIdWithFilter(Long bookerId, BookingState state);

    List<BookingDto> getBookingsByOwnerIdWithFilter(Long ownerId, BookingState state);

}