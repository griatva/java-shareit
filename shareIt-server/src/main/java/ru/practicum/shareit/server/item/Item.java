package ru.practicum.shareit.server.item;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.server.user.User;

@Table(name = "items")
@Entity
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;
    private String description;
    private Boolean available;

    @Column(name = "request_id")
    private Long requestId;

}
