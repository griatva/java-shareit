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
        log.debug("Creating item request [{}], requestorId = [{}]", itemRequestDto, requestorId);
        return itemRequestService.create(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemInfoDto> getAllByRequestorIdWithSort(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.debug("Retrieving all requests for requestorId = [{}] with information about proposed items",
                requestorId);
        return itemRequestService.getAllByRequestorIdWithSort(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllWithSort() {
        log.debug("Retrieving all item requests");
        return itemRequestService.getAllWithSort();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemInfoDto getById(@PathVariable Long requestId) {
        log.debug("Retrieving item request with id = [{}] with information about proposed items", requestId);
        return itemRequestService.getById(requestId);
    }
}
