package ru.practicum.shareit.server.request;

import ru.practicum.shareit.server.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestDto create(Long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemInfoDto> getAllByRequestorIdWithSort(Long requestorId);

    List<ItemRequestDto> getAllWithSort();

    ItemRequestWithItemInfoDto getById(Long requestId);
}
