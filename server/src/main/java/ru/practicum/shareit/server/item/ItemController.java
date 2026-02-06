package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.dto.ItemWithBookingsDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Creating item [{}], ownerId = [{}]", itemDto, ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestBody ItemUpdateDto updates,
                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Updating item with id = [{}]", itemId);
        return itemService.update(itemId, updates, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                       @PathVariable long itemId) {
        log.debug("Retrieving item with id = [{}]", itemId);
        return itemService.getById(itemId, requesterId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllItemsByOwnerWithBookings(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Retrieving all items for userId = [{}] with booking dates", ownerId);
        return itemService.getAllItemsByOwnerWithBookings(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllItemsByText(@RequestParam String text) {
        log.debug("Searching available items by substring = [{}]", text);
        return itemService.getAllItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.debug("Creating comment for itemId = [{}] by authorId = [{}]", itemId, authorId);
        return itemService.createComment(authorId, itemId, commentDto);
    }
}
