package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String text;

    private Item item;

    private User author;

    private String authorName;

    private LocalDateTime created;
}
