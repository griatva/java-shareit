package practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.item.dto.CommentDto;
import practicum.item.dto.ItemDto;
import practicum.item.dto.ItemUpdateDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.debug("Creating item [{}], ownerId = [{}]", itemDto, ownerId);
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId,
                                         @Valid @RequestBody ItemUpdateDto updates,
                                         @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Updating item with id = [{}]", itemId);
        return itemClient.update(itemId, updates, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                          @PathVariable long itemId) {
        log.debug("Retrieving item with id = [{}]", itemId);
        return itemClient.getById(itemId, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerWithBookings(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Retrieving all items for userId = [{}] with booking dates", ownerId);
        return itemClient.getAllItemsByOwnerWithBookings(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllItemsByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam String text) {
        log.debug("Searching all available items by substring = [{}]", text);
        return itemClient.getAllItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                                @PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.debug("Creating comment for itemId = [{}] by authorId = [{}]", itemId, authorId);
        return itemClient.createComment(authorId, itemId, commentDto);
    }
}
