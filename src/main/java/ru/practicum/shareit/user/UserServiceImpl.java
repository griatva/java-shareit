package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        userRepository.existsByEmail(userDto.getId(), userDto.getEmail());
        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Long id, Map<String, String> updates) {

        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        Map<String, String> validatedUpdates = validateUpdates(id, updates);
        return UserMapper.toUserDto(userRepository.update(id, validatedUpdates));
    }


    private Map<String, String> validateUpdates(Long id, Map<String, String> updates) {
        if (updates == null || updates.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> validatedUpdates = new HashMap<>();

        updates.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                switch (key) {
                    case "name":
                        validatedUpdates.put(key, value);
                        break;
                    case "email":
                        userRepository.existsByEmail(id, value);
                        validatedUpdates.put(key, value);
                        break;
                    default:
                        throw new ValidationException("Поле " + key + " не поддерживается для обновления");
                }
            }
        });
        return validatedUpdates;
    }

    @Override
    public UserDto getById(long id) {
        return userRepository.getById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}
