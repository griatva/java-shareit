package ru.practicum.shareit.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository {

    Item create(Long ownerId, Item item);

    Item update(Long itemId, Map<String, String> updates);

    Optional<Item> getById(Long itemId);

    List<Item> getAllItemsByOwnerId(Set<Long> itemIds);

    List<Item> getAllItemsByText(String text);

}
