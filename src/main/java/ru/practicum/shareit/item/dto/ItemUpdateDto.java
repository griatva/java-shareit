package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateDto {

    @NotEmpty(message = "Название не может быть пустым или содержать только пробелы")
    private String name;

    @NotEmpty(message = "Описание не может быть пустым или содержать только пробелы")
    @Size(max = 300, message = "Длина описания должна быть не более 300 символов")
    private String description;

    private Boolean available;
}
