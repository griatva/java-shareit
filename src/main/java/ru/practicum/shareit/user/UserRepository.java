package ru.practicum.shareit.user;

import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    User update(long id, Map<String, String> updates);

    Optional<User> getById(long id);

    void deleteById(long id);

    void existsByEmail(Long id, String email);
}
