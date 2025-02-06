package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {

    private long id;
    private String description;
    private long requestorId;
    private LocalDateTime created;

}

