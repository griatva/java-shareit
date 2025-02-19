package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long id, Map<String, String> updates);

    UserDto getById(long id);

    void deleteById(long id);
}
