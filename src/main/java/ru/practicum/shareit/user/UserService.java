package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

interface UserService {

    List<User> getAll();

    User getById(long id);

    User addUser(User user);

    User updateUser(long id, User user);

    void deleteUser(long id);

}
