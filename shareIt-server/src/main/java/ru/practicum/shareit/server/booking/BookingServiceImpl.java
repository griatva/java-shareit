package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.enums.BookingState;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.exception.ForbiddenExcepton;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(Long bookerId, BookingDto bookingDto) {
        User booker = findUserById(bookerId);
        Item item = findItemById(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь недоступна для бронирования на эти даты");
        }
        bookingDto.setStatus(Status.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveOrRejectBooking(Long ownerId, Long bookingId, Boolean approved) {
        findUserById(ownerId);
        Booking booking = findBookingById(bookingId);
        Item item = booking.getItem();
        findItemById(item.getId());

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Согласовывать бронирование может только владелец вещи");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long requesterId, Long bookingId) {
        findUserById(requesterId);
        Booking booking = findBookingById(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(requesterId) && !ownerId.equals(requesterId)) {
            throw new ValidationException("Получить бронирование может только владелец или заказчик вещи");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBookerIdWithFilter(Long bookerId, BookingState state) {
        findUserById(bookerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Неизвестный статус бронирования: " + state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getBookingsByOwnerIdWithFilter(Long ownerId, BookingState state) {
        User owner = findUserById(ownerId);
        if (owner.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный статус бронирования: " + state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenExcepton("Пользователь с id " + userId + " не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }
}

