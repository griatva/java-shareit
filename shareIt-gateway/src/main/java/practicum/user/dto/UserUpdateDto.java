package practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserUpdateDto {

    @NotEmpty(message = "Имя не может быть пустым или содержать только пробелы")
    private String name;

    @NotEmpty(message = "Адрес электронной почты не может быть пустым или содержать только пробелы")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;
}
