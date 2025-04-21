package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DoubleEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id;


    @Override
    public List<UserDto> getAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        checkDuplicateEmail(userDto);
        User user = new User();
        user.setId(getNextId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (!users.containsKey(userDto.getId())) {
            throw new NotFoundException("Пользователь с ID: " + userDto.getId() + " не найден.");
        }
        User oldUser = users.get(userDto.getId());
        if (userDto.getEmail() != null) {
            checkDuplicateEmail(userDto);
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        users.put(oldUser.getId(), oldUser);
        return UserMapper.toUserDto(oldUser);
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

    private void checkDuplicateEmail(UserDto userDto) {
        String email = userDto.getEmail();
        boolean noEmailExists = users.values().stream()
                .noneMatch(u -> u.getEmail().equals(email));
        if (!noEmailExists) {
            throw new DoubleEmailException("Указанный email уже есть в списке.");
        }
    }

}
