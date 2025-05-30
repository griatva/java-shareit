package ru.practicum.shareit.server.item;


import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long itemId, ItemUpdateDto updates, Long ownerId);

    ItemWithBookingsDto getById(Long itemId, Long requesterId);

    List<ItemWithBookingsDto> getAllItemsByOwnerWithBookings(Long ownerId);

    List<ItemDto> getAllItemsByText(String text);

    CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto);
}
