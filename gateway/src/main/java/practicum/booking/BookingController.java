package practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.booking.dto.BookingDto;
import practicum.booking.enums.BookingState;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                         @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Создание бронирования [{}], id заказчика = [{}]", bookingDto, bookerId);
        return bookingClient.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                         @PathVariable long bookingId,
                                                         @RequestParam Boolean approved) {
        log.debug("Подтверждение или отклонение запроса на бронирование с id = [{}], " +
                "id владельца = [{}], isApproved = [{}]", bookingId, ownerId, approved);
        return bookingClient.approveOrRejectBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                          @PathVariable long bookingId) {
        log.debug("Получение бронирования с id = [{}] пользователем с id = [{}]",
                bookingId, requesterId);
        return bookingClient.getById(requesterId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerIdWithFilter(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                                  @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.debug("Получение всех своих бронирований со статусом для фильтрации = [{}] заказчиком с id = [{}]",
                state, bookerId);
        return bookingClient.getBookingsByBookerIdWithFilter(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerIdWithFilter(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                 @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.debug("Получение всех своих бронирований со статусом для фильтрации = [{}] владельцем с id = [{}]",
                state, ownerId);
        return bookingClient.getBookingsByOwnerIdWithFilter(ownerId, state);
    }

}
