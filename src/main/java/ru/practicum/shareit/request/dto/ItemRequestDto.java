package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {

    private long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 300, message = "Длина описания должна быть не более 300 символов")
    private String description;

    @NotNull(message = "Это поле обязательно для заполнения")
    private long requestorId;

    private LocalDateTime created;

}
