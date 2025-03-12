package practicum.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 300, message = "Длина описания должна быть не более 300 символов")
    private String description;

    private LocalDateTime created;

}
