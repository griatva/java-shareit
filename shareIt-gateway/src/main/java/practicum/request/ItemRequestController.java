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
        log.debug("Создание запроса [{}], id заказчика = [{}]", itemRequestDto, requestorId);
        return itemRequestClient.create(requestorId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestorIdWithSort(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.debug("Получение всех запросов пользователя с id = [{}] с информацией о преложенных вещах", requestorId);
        return itemRequestClient.getAllByRequestorIdWithSort(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithSort (@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получение всех запросов");
        return itemRequestClient.getAllWithSort(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long requestId) {
        log.debug("Получение запроса с id = [{}] с информацией о преложенных вещах", requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
