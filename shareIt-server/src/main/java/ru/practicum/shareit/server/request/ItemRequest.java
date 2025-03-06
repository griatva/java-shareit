package ru.practicum.shareit.server.request;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

@Table(name = "requests")
@Entity
@Data
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "create_date")
    private LocalDateTime createDate;

}

