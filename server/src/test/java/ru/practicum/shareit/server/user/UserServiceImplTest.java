package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.server.exception.DoubleEmailException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");
    private final User user = new User();

    @Test
    void getAll_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(fillUser(user)));
        List<UserDto> result = userService.getAll();
        assertEquals(1, result.size());
        assertEquals(userDto, result.getFirst());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fillUser(user)));
        UserDto result = userService.getById(1L);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(99L))
                .thenThrow(new NotFoundException("User not found"));
        assertThrows(NotFoundException.class, () -> userService.getById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void addUser_shouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(fillUser(user));
        UserDto result = userService.addUser(userDto);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        UserDto updateDto = new UserDto(1L, "Updated Name", "updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(fillUser(user)));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserDto result = userService.updateUser(1L, updateDto);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateOnlyNameWhenEmailIsNull() {
        UserDto updateDto = new UserDto(1L, "Updated Name", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(fillUser(user)));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserDto result = userService.updateUser(1L, updateDto);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail()); // Email остался прежним
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowDoubleEmailExceptionWhenEmailExists() {
        UserDto updateDto = new UserDto(1L, "John Doe", "existing@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(fillUser(user)));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        assertThrows(DoubleEmailException.class, () -> userService.updateUser(1L, updateDto));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(99L, userDto));
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);
        ResponseEntity<Object> response = userService.deleteUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).deleteById(any());
    }

    private User fillUser(User user) {
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        return user;
    }
}