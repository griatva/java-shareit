package practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class UserUpdateDto {


    @Nullable
    @Pattern(regexp = "^(?!\\s*$).+", message = "Поле не должно быть пустым или содержать только пробелы")
    private String name;

    @Nullable
    @Pattern(regexp = "^(?!\\s*$).+", message = "Поле не должно быть пустым или содержать только пробелы")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;
}
