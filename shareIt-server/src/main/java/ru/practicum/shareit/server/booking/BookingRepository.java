package ru.practicum.shareit.server.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByBookingStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndBookingStartBeforeAndBookingEndAfterOrderByBookingStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndBookingEndBeforeOrderByBookingStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndBookingStartAfterOrderByBookingStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByBookingStartDesc(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByBookingStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndBookingStartBeforeAndBookingEndAfterOrderByBookingStartDesc(Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndBookingEndBeforeOrderByBookingStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndBookingStartAfterOrderByBookingStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByBookingStartDesc(Long ownerId, Status status);

    List<Booking> findByItemIdIn(List<Long> itemIds);

    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findByItemId(Long itemId);
}
