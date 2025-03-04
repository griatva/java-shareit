package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

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
    public ItemWithBookingsDto getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                       @PathVariable long itemId) {
        log.debug("Получение вещи с id = [{}]", itemId);
        return itemService.getById(itemId, requesterId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllItemsByOwnerWithBookings(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Получение всех вещей пользователя с id = [{}] с датами бронирования", ownerId);
        return itemService.getAllItemsByOwnerWithBookings(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemsByText(@RequestParam String text) {
        log.debug("Получение всех доступных вещей по подстроке = [{}]", text);
        return itemService.getAllItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.debug("Создание отзыва для вещи с id = [{}] автором с id = [{}]", itemId, authorId);
        return itemService.createComment(authorId, itemId, commentDto);
    }
}
