package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll();
    }

    @Override
    public UserDto getById(long id) {
        return userRepository.getById(id);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        return userRepository.addUser(userDto);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        userDto.setId(id);
        return userRepository.updateUser(userDto);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }
}
