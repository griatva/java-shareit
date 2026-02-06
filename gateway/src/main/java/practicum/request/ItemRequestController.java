package practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Creating item request [{}], requestorId = [{}]", itemRequestDto, requestorId);
        return itemRequestClient.create(requestorId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestorIdWithSort(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.debug("Retrieving all requests for requestorId = [{}] with information about proposed items", requestorId);
        return itemRequestClient.getAllByRequestorIdWithSort(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithSort(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Retrieving all item requests");
        return itemRequestClient.getAllWithSort(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long requestId) {
        log.debug("Retrieving item request with id = [{}] with information about proposed items", requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
