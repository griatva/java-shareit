package practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 300, message = "Description length must not exceed 300 characters")
    private String description;

    @NotNull(message = "Field is required")
    private Boolean available;

    private Long requestId;

    private List<CommentDto> comments;
}
