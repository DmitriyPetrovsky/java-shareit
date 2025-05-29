package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.BadRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.comment.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = new CommentDto(1L,
            "Володя",
            "Лучшая бензопила в мире",
            LocalDateTime.of(2025, 2, 1, 12, 0));
    private final ItemDto itemDto = new ItemDto(1L,
            "Бензопила Дружба",
            "Мощная, шумная, пилит",
            true,
            2L,
            null,
            null,
            List.of(commentDto),
            4L);

    @Test
    void getItemsByUserIdTest() throws Exception {
        when(itemService.getItemsByUserId(2L))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$[0].nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments.length()").value(1))
                .andExpect(jsonPath("$[0].comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$[0].comments[0].text").value(commentDto.getText()))
                .andExpect(jsonPath("$[0].comments[0].created").value(
                        commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()));
    }

    @Test
    void createItemTest() throws Exception {
        itemDto.setComments(new ArrayList<>());
        when(itemService.createItem(2L, itemDto))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(0))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void updateItem_successfulTest() throws Exception {
        long itemId = 1L;
        when(itemService.updateItem(itemId, 2L, itemDto))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void updateItem_withWrongUserIdTest() throws Exception {
        long itemId = 1L;
        when(itemService.updateItem(itemId, 3L, itemDto))
                .thenThrow(new NotFoundException("Юзер не найден"));
        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Юзер не найден"));
    }

    @Test
    void updateItem_withWrongItemIdTest() throws Exception {
        long itemId = 3L;
        when(itemService.updateItem(itemId, 2L, itemDto))
                .thenThrow(new NotFoundException("Итем не найден"));
        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Итем не найден"));
    }

    @Test
    void getItemById_successfulTest() throws Exception {
        long itemId = 1L;
        when(itemService.getItemById(1L, 2L))
                .thenReturn(itemDto);
        mvc.perform(get("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$.comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.comments[0].text").value(commentDto.getText()))
                .andExpect(jsonPath("$.comments[0].created").value(
                        commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void getNotExistsItemByIdTest() throws Exception {
        long itemId = 1L;
        when(itemService.getItemById(1L, 2L))
                .thenThrow(new NotFoundException("Итем не найден"));
        mvc.perform(get("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Итем не найден"));
    }

    @Test
    void searchItemTest() throws Exception {
        String text = "Бензопила";
        when(itemService.searchItem(text))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text={text}", text)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$[0].nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments.length()").value(1))
                .andExpect(jsonPath("$[0].comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$[0].comments[0].text").value(commentDto.getText()))
                .andExpect(jsonPath("$[0].comments[0].created").value(
                        commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.postComment(commentDto, 2L, itemDto.getId()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.created").value(
                        commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void addCommentByWrongUserTest() throws Exception {
        when(itemService.postComment(commentDto, 3L, itemDto.getId()))
                .thenThrow(new BadRequestException("Пользователь не может комментировать итем"));
        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Пользователь не может комментировать итем"));
    }
}
