package practicum.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateDto {

    @NotEmpty(message = "Name must not be empty or contain only whitespace")
    private String name;

    @NotEmpty(message = "Description must not be empty or contain only whitespace")
    @Size(max = 300, message = "Description length must not exceed 300 characters")
    private String description;

    private Boolean available;

}
