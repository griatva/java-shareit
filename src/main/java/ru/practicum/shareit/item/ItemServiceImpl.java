package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.ForbiddenExcepton;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User user = findUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long itemId, ItemUpdateDto updates, Long ownerId) {

        Item item = findItemById(itemId);
        findUserById(ownerId);
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenExcepton("Редактировать вещь может только ее владелец");
        }
        if (updates.getName() == null && updates.getDescription() == null && updates.getAvailable() == null) {
            return ItemMapper.toItemDto(item);
        }

        if (updates.getName() != null) {
            item.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            item.setDescription(updates.getDescription());
        }
        if (updates.getAvailable() != null) {
            item.setAvailable(updates.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingsDto getById(Long itemId, Long requesterId) {
        Item item = findItemById(itemId);
        findUserById(requesterId);
        if (requesterId.equals(item.getOwner().getId())) {
            List<Booking> itemBookings = bookingRepository.findByItemId(itemId);
            return getItemWithBookingsDto(item, itemBookings);
        } else {
            List<CommentDto> commentsDto = CommentMapper.toCommentDtoList(commentRepository.findByItemId(item.getId()));
            BookingShortDto lastBooking = null;
            BookingShortDto nextBooking = null;
            return ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, commentsDto);
        }
    }

    @Override
    public List<ItemWithBookingsDto> getAllItemsByOwnerWithBookings(Long ownerId) {
        findUserById(ownerId);
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findByItemIdIn(itemIds);
        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());
                    return getItemWithBookingsDto(item, itemBookings);
                })
                .collect(Collectors.toList());
    }

    private ItemWithBookingsDto getItemWithBookingsDto(Item item, List<Booking> itemBookings) {

        BookingShortDto lastBooking = itemBookings.stream()
                .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .map(b -> new BookingShortDto(b.getStart(), b.getEnd()))
                .orElse(null);

        BookingShortDto nextBooking = itemBookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(b -> new BookingShortDto(b.getStart(), b.getEnd()))
                .orElse(null);

        List<CommentDto> commentsDto = CommentMapper.toCommentDtoList(commentRepository.findByItemId(item.getId()));

        return ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.searchByText(text));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private Booking findBookingByBookerIdAndItemId(Long bookerId, Long itemId) {
        return bookingRepository.findByBookerIdAndItemId(bookerId, itemId)
                .orElseThrow(() -> new NotFoundException("Бронирование заказчиком с id " + bookerId +
                        " вещи с id " + itemId + " не найдено"));
    }

    @Override
    public CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {

        Booking booking = findBookingByBookerIdAndItemId(authorId, itemId);
        if (!booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Создание отзыва возможно только после окончания срока аренды");
        }

        commentDto.setAuthor(findUserById(authorId));
        commentDto.setItem(findItemById(itemId));
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = CommentMapper.toComment(commentDto);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
