package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DoubleEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private Map<Long, User> users = new HashMap<>();
    private long id;


    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        checkDuplicateEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с ID: " + user.getId() + " не найден.");
        }
        User oldUser = users.get(user.getId());
        if (user.getEmail() != null) {
            checkDuplicateEmail(user);
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    @Override
    public void deleteUser(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден.");
        }
        users.remove(id);
    }

    private long getNextId() {
        return id++;
    }

    private void checkDuplicateEmail(User user) {
        String email = user.getEmail();
        boolean noEmailExists = users.values().stream()
                .allMatch(u -> !u.getEmail().equals(email));
        if (!noEmailExists) {
            throw new DoubleEmailException("Указанный email уже есть в списке.");
        }
    }

}
