package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long bookerId,
                             @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Создание бронирования [{}], id заказчика = [{}]", bookingDto, bookerId);
        return bookingService.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        log.debug("Подтверждение или отклонение запроса на бронирование с id = [{}], " +
                "id владельца = [{}], isApproved = [{}]", bookingId, ownerId, approved);
        return bookingService.approveOrRejectBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                              @PathVariable long bookingId) {
        log.debug("Получение бронирования с id = [{}] пользователем с id = [{}]",
                bookingId, requesterId);
        return bookingService.getById(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerIdWithFilter(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.debug("Получение всех своих бронирований со статусом для фильтрации = [{}] заказчиком с id = [{}]",
                state, bookerId);
        return bookingService.getBookingsByBookerIdWithFilter(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerIdWithFilter(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                           @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.debug("Получение всех своих бронирований со статусом для фильтрации = [{}] владельцем с id = [{}]",
                state, ownerId);
        return bookingService.getBookingsByOwnerIdWithFilter(ownerId, state);
    }

}
