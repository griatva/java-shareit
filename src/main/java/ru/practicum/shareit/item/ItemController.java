package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.debug("Создание вещи [{}], id владельца = [{}]", itemDto, ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestBody ItemUpdateDto updates,
                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Обновление вещи с id = [{}]", itemId);
        return itemService.update(itemId, updates, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        log.debug("Получение вещи с id = [{}]", itemId);
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Получение всех вещей пользователя с id = [{}]", ownerId);
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemsByText(@RequestParam String text) {
        log.debug("Получение всех доступных вещей по подстроке = [{}]", text);
        return itemService.getAllItemsByText(text);
    }
}
