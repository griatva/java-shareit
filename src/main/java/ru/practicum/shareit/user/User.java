package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.util.HashSet;
import java.util.Set;

@Table(name = "users", schema = "public")
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Item> items = new HashSet<>();
}
