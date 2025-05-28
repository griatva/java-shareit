package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemInfoDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Создание запроса [{}], id заказчика = [{}]", itemRequestDto, requestorId);
        return itemRequestService.create(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemInfoDto> getAllByRequestorIdWithSort(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.debug("Получение всех запросов пользователя с id = [{}] с информацией о преложенных вещах", requestorId);
        return itemRequestService.getAllByRequestorIdWithSort(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllWithSort() {
        log.debug("Получение всех запросов");
        return itemRequestService.getAllWithSort();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemInfoDto getById(@PathVariable Long requestId) {
        log.debug("Получение запроса с id = [{}] с информацией о преложенных вещах", requestId);
        return itemRequestService.getById(requestId);
    }
}
