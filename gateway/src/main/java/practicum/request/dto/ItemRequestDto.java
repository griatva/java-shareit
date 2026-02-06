package practicum.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 300, message = "Description length must not exceed 300 characters")
    private String description;

    private LocalDateTime created;

}
