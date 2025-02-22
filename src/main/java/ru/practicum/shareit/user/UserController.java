package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.debug("Создание пользователя [{}]", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserUpdateDto updates) {
        log.debug("Обновление пользователя с id = [{}]", id);
        return userService.update(id, updates);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.debug("Получение пользователя с id = [{}]", id);
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.debug("Удаление пользователя с id = [{}]", id);
        userService.deleteById(id);
    }
}
