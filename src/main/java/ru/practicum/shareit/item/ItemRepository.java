package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository {

    Item create(Long ownerId, Item item);

    Item update(Long itemId, ItemUpdateDto updates);

    Optional<Item> getById(Long itemId);

    List<Item> getItemsByIds(Set<Long> itemIds);

    List<Item> getAllItemsByText(String text);

}
