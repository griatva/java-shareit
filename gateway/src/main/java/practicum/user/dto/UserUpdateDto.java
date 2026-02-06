package practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class UserUpdateDto {


    @Nullable
    @Pattern(regexp = "^(?!\\s*$).+", message = "Field must not be blank")
    private String name;

    @Nullable
    @Pattern(regexp = "^(?!\\s*$).+", message = "Field must not be blank")
    @Email(message = "Invalid email address")
    private String email;
}
