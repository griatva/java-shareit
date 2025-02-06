package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User user = findUserById(ownerId);
        Item item = itemRepository.create(ownerId, ItemMapper.toItem(itemDto));
        user.getItemIds().add(item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, ItemUpdateDto updates, Long ownerId) {

        Item item = findItemById(itemId);
        findUserById(ownerId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Редактировать вещь может только ее владелец");
        }
        if (updates.getName() == null && updates.getDescription() == null && updates.getAvailable() == null) {
            return ItemMapper.toItemDto(item);
        }
        return ItemMapper.toItemDto(itemRepository.update(itemId, updates));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(findItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        User user = findUserById(ownerId);
        Set<Long> itemIds = user.getItemIds();
        return ItemMapper.toItemDtoList(itemRepository.getItemsByIds(itemIds));
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.getAllItemsByText(text));
    }

    private User findUserById(Long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }
}
