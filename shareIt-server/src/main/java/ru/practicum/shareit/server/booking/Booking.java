package ru.practicum.shareit.server.booking;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime bookingStart;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime bookingEnd;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
