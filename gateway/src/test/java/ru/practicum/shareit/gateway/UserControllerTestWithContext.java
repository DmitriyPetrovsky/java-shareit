package ru.practicum.shareit.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.user.UserClient;
import ru.practicum.shareit.gateway.user.UserController;
import ru.practicum.shareit.gateway.user.dto.UserDto;


import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTestWithContext {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = new UserDto(1L, "Bill Gates", "gates@microsoft.com");

    @Test
    void saveNewUser() throws Exception {
        when(userClient.addUser(any()))
                .thenReturn(ResponseEntity.ok().body(userDto));
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
    void saveNewUserWithWrongEmail() throws Exception {
        UserDto dto = new UserDto(1L, "Bill Gates", "@microsoft.com");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void saveNewUserWithEmptyName() throws Exception {
        UserDto dto = new UserDto(1L, "", "gates@microsoft.com");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getUserById_success() throws Exception {
        long userId = 1L;
        UserDto expectedUser = new UserDto(userId, "Ivan", "ivan@example.com");
        when(userClient.getById(userId))
                .thenReturn(ResponseEntity.ok().body(expectedUser));
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUser.getId()))
                .andExpect(jsonPath("$.name").value(expectedUser.getName()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()));
    }

    @Test
    void getUserById_userNotFound() throws Exception {
        long userId = 999L;
        when(userClient.getById(userId))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_success() throws Exception {
        long userId = 1L;
        UserDto requestDto = new UserDto(userId, "Old Ivan", "old@email.com");
        UserDto updatedDto = new UserDto(userId, "New Ivan", "new@email.com");
        when(userClient.updateUser(userId, requestDto))
                .thenReturn(ResponseEntity.ok().body(updatedDto));
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
        UserDto requestDto = new UserDto(userId, "Old Ivan", "old@email.com");
        UserDto updatedDto = new UserDto(userId, "New Ivan", "new@email.com");
        when(userClient.updateUser(userId, requestDto))
                .thenReturn(ResponseEntity.ok().body(updatedDto));
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedDto.getName()))
                .andExpect(jsonPath("$.email").value(updatedDto.getEmail()));
    }
}
