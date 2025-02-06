package ru.practicum.shareit.item;

import lombok.Data;

@Data
public class Item {

    private long id;
    private long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;

}
