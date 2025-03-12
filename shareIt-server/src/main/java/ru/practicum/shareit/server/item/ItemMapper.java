package ru.practicum.shareit.server.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemProposedDto;
import ru.practicum.shareit.server.item.dto.ItemWithBookingsDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item,
                                                            BookingShortDto lastBooking,
                                                            BookingShortDto nextBooking,
                                                            List<CommentDto> commentsDto) {
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(item.getId());
        itemWithBookingsDto.setName(item.getName());
        itemWithBookingsDto.setDescription(item.getDescription());
        itemWithBookingsDto.setAvailable(item.getAvailable());
        itemWithBookingsDto.setRequestId(item.getRequestId());
        itemWithBookingsDto.setLastBooking(lastBooking);
        itemWithBookingsDto.setNextBooking(nextBooking);
        itemWithBookingsDto.setComments(commentsDto);
        return itemWithBookingsDto;
    }

    public static ItemProposedDto toItemProposedDto(Item item) {
        ItemProposedDto dto = new ItemProposedDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }

    public static List<ItemProposedDto> toItemProposedDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemProposedDto)
                .collect(Collectors.toList());
    }

}
