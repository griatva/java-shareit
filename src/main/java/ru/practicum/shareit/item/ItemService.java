package ru.practicum.shareit.item;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long itemId, Map<String, String> updates, Long ownerId);

    ItemDto getById(Long itemId);

    List<ItemDto> getAllItemsByOwner(Long ownerId);

    List<ItemDto> getAllItemsByText(String text);
}
