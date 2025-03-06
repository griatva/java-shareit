package ru.practicum.shareit.server.item.dto;

import lombok.Data;

@Data
public class ItemProposedDto {

        private Long id;

        private String name;

        private Long ownerId;
}
