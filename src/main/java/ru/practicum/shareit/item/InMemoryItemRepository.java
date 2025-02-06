package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long counterId = 0;

    private long generateItemId() {
        return ++counterId;
    }

    @Override
    public Item create(Long ownerId, Item item) {
        item.setId(generateItemId());
        item.setOwnerId(ownerId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, ItemUpdateDto updates) {
        Item item = items.get(itemId);

        if (updates.getName() != null) {
            item.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            item.setDescription(updates.getDescription());
        }
        if (updates.getAvailable() != null) {
            item.setAvailable(updates.getAvailable());
        }

        return item;
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByIds(Set<Long> itemIds) {

        return items.entrySet().stream()
                .filter(entry -> itemIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItemsByText(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}