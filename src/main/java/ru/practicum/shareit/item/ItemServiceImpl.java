package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User user = userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        Item item = itemRepository.create(ownerId, ItemMapper.toItem(itemDto));
        user.getItemIds().add(item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, Map<String, String> updates, Long ownerId) {

        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        User user = userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        if (item.getOwnerId() != ownerId) {
            throw new ValidationException("Редактировать вещь может только ее владелец");
        }
        Map<String, String> validatedUpdates = validateUpdates(updates);
        return ItemMapper.toItemDto(itemRepository.update(itemId, validatedUpdates));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemRepository.getById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        User user = userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        Set<Long> itemIds = user.getItemIds();
        return itemRepository.getAllItemsByOwnerId(itemIds)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getAllItemsByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Map<String, String> validateUpdates(Map<String, String> updates) {
        if (updates == null || updates.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> validatedUpdates = new HashMap<>();

        updates.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                switch (key) {
                    case "name":
                    case "description":
                        validatedUpdates.put(key, value);
                        break;
                    case "available":
                        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                            validatedUpdates.put(key, value);
                        } else {
                            throw new ValidationException("Значение для поля 'available' должно быть 'true' или 'false'");
                        }
                        break;
                    default:
                        throw new ValidationException("Поле " + key + " не поддерживается для обновления");
                }
            }
        });
        return validatedUpdates;
    }
}
