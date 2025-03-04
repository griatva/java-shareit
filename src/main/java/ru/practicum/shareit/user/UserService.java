package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserUpdateDto updates);

    UserDto getById(long id);

    void deleteById(long id);
}
