package ru.practicum.shareit.user;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    private String name;
    private String email;
    private Set<Long> itemIds = new HashSet<>();
}
