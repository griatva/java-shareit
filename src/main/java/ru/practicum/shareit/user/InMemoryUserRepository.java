package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long counterId = 0;

    private long generateItemId() {
        return ++counterId;
    }

    @Override
    public User create(User user) {
        user.setId(generateItemId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(long id, Map<String, String> updates) {
        User user = users.get(id);

        if (updates == null || updates.isEmpty()) {
            return user;
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName(value);
                    break;
                case "email":
                    user.setEmail(value);
                    break;
            }
        });
        return user;
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    public void existsByEmail(Long id, String email) {
        boolean emailExists = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && user.getId() != id);

        if (emailExists) {
            throw new DuplicatedDataException("Email уже используется другим пользователем");
        }
    }
}
