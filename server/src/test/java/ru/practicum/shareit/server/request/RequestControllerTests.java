package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTests {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto requestDto = new ItemRequestDto(1L,
            2L,
            "Test Request",
            null,
            LocalDateTime.now());

    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(requestDto))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.requestorId").value(requestDto.getRequestorId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.items").value(requestDto.getItems()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getRequestsTest() throws Exception {
        when(itemRequestService.getRequests(2L))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].requestorId").value(requestDto.getRequestorId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$[0].items").value(requestDto.getItems()))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(itemRequestService.getAllRequests(2L))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].requestorId").value(requestDto.getRequestorId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$[0].items").value(requestDto.getItems()))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.findById(2L, 1))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.requestorId").value(requestDto.getRequestorId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.items").value(requestDto.getItems()))
                .andExpect(jsonPath("$.created").exists());
    }
}
