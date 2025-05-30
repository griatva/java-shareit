package practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 300, message = "Длина описания должна быть не более 300 символов")
    private String description;

    @NotNull(message = "Это поле обязательно для заполнения")
    private Boolean available;

    private Long requestId;

    private List<CommentDto> comments;
}
