package ru.practicum.shareit.server.user;


import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(long id);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long id, UserDto userDto);

    ResponseEntity<Object> deleteUser(long id);

}
