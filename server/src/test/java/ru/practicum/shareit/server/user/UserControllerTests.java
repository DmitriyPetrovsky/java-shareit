package ru.practicum.shareit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(1L, "Bill Gates", "gates@microsoft.com");

    @Test
    void saveNewUser() throws Exception {
        when(userService.addUser(any()))
            .thenReturn(userDto);
        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUserById_success() throws Exception {
        long userId = 1L;
        UserDto expectedUser = new UserDto(userId, "Ivan", "ivan@example.com");
        when(userService.getById(userId))
                .thenReturn(expectedUser);
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUser.getId()))
                .andExpect(jsonPath("$.name").value(expectedUser.getName()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()));
    }

    @Test
    void getUserById_userNotFound() throws Exception {
        long userId = 999L;
        when(userService.getById(userId))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_success() throws Exception {
        long userId = 1L;
        UserDto requestDto = new UserDto(userId, "Old Ivan", "old@email.com");
        UserDto updatedDto = new UserDto(userId, "New Ivan", "new@email.com");
        when(userService.updateUser(userId, requestDto))
                .thenReturn(updatedDto);
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedDto.getEmail()));
    }

    @Test
    void updateUser_unknown() throws Exception {
        long userId = 999L;
        UserDto dto = new UserDto(userId, "Ivan", "ivan@email.com");
        when(userService.updateUser(userId, dto))
                .thenThrow(NotFoundException.class);
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_success() throws Exception {
        long userId = 1L;
        when(userService.deleteUser(userId))
                .thenReturn(ResponseEntity.ok().build());
        mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_unknown() throws Exception {
        long userId = 1L;
        when(userService.deleteUser(userId))
                .thenThrow(NotFoundException.class);
        mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }
}
