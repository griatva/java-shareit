package ru.practicum.shareit.server.request.dto;

import lombok.Data;
import ru.practicum.shareit.server.item.dto.ItemProposedDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestWithItemInfoDto {

        private Long id;

        private String description;

        private LocalDateTime created;

        private List<ItemProposedDto> items;

}
