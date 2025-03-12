package ru.practicum.shareit.server.request;

import ru.practicum.shareit.server.item.dto.ItemProposedDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemInfoDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreateDate());
        return dto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreateDate(dto.getCreated());
        return itemRequest;
    }

    public static ItemRequestWithItemInfoDto toItemRequestWithItemInfoDto(ItemRequest itemRequest,
                                                                          List<ItemProposedDto> proposedItems) {
        ItemRequestWithItemInfoDto dto = new ItemRequestWithItemInfoDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreateDate());
        dto.setItems(proposedItems);
        return dto;
    }

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}