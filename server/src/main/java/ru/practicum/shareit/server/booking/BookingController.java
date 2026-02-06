package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.enums.BookingState;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long bookerId,
                             @RequestBody BookingDto bookingDto) {
        log.debug("Creating booking [{}], bookerId = [{}]", bookingDto, bookerId);
        return bookingService.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        log.debug("Approving or rejecting booking request with id = [{}], ownerId = [{}], approved = [{}]",
                bookingId, ownerId, approved);
        return bookingService.approveOrRejectBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                              @PathVariable long bookingId) {
        log.debug("Retrieving booking with id = [{}] by user with id = [{}]",
                bookingId, requesterId);
        return bookingService.getById(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerIdWithFilter(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.debug(
                "Retrieving all bookings for bookerId = [{}] with state filter = [{}]",
                bookerId, state
        );
        return bookingService.getBookingsByBookerIdWithFilter(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerIdWithFilter(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.debug(
                "Retrieving all bookings for ownerId = [{}] with state filter = [{}]",
                ownerId, state
        );
        return bookingService.getBookingsByOwnerIdWithFilter(ownerId, state);
    }

}
