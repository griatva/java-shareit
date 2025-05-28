package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.dto.ItemProposedDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestWithItemInfoDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemProposedDto> items;

}
