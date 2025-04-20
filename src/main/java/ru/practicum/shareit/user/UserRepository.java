package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User getById(long id);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

}
