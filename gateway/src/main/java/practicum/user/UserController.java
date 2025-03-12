package practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.user.dto.UserDto;
import practicum.user.dto.UserUpdateDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.debug("Создание пользователя [{}]", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id,
                                         @Valid @RequestBody UserUpdateDto updates) {
        log.debug("Обновление пользователя с id = [{}]", id);
        return userClient.update(id, updates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.debug("Получение пользователя с id = [{}]", id);
        return userClient.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.debug("Удаление пользователя с id = [{}]", id);
        userClient.deleteById(id);
    }
}
