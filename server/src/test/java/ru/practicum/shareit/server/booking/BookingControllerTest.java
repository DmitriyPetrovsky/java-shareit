package ru.practicum.shareit.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.server.booking.enums.State;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.exception.ForbiddenUserException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto item = new ItemDto(1L,
            "Бензопила Дружба",
            "Мощная, шумная, пилит",
            true,
            2L,
            null,
            null,
            new ArrayList<>(),
            4L);
    private final UserDto booker = new UserDto(1L, "Bill Gates", "gates@microsoft.com");
    private final BookingDtoResponse bookResp = new BookingDtoResponse(
            1L,
            LocalDateTime.of(2025,12,1,10,0),
            LocalDateTime.of(2025,12,1,12,0),
            item,
            booker,
            Status.WAITING
            );
    private final BookingDto bookReq = new BookingDto(
            1L,
            LocalDateTime.of(2025,12,1,10,0),
            LocalDateTime.of(2025,12,1,12,0),
            2L,
            1L,
            Status.WAITING);

    @Test
    void bookSuccessfulTest() throws Exception {
        when(bookingService.book(bookReq))
                .thenReturn(bookResp);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookResp.getId()))
                .andExpect(jsonPath("$.start").value(
                        bookResp.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(
                        bookResp.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.item.id").value(bookResp.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookResp.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookResp.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookResp.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.ownerId").value(bookResp.getItem().getOwnerId()))
                .andExpect(jsonPath("$.item.lastBooking").value(bookResp.getItem().getLastBooking()))
                .andExpect(jsonPath("$.item.nextBooking").value(bookResp.getItem().getNextBooking()))
                .andExpect(jsonPath("$.item.requestId").value(bookResp.getItem().getRequestId()))
                .andExpect(jsonPath("$.booker.id").value(bookResp.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookResp.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookResp.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookResp.getStatus().toString()));
    }

    @Test
    void bookUnExistedItemTest() throws Exception {
        when(bookingService.book(bookReq))
                .thenThrow(new NotFoundException("Item not found"));
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Item not found"));
    }

    @Test
    void approveBookingTest() throws Exception {
        boolean approve = true;
        when(bookingService.approve(1L, 1L, approve))
                .thenReturn(bookResp);
        mvc.perform(patch("/bookings/{bookingId}?approved={approve}", 1L, approve)
                        .content(mapper.writeValueAsString(bookReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookResp.getId()))
                .andExpect(jsonPath("$.start").value(
                        bookResp.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(
                        bookResp.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.item.id").value(bookResp.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookResp.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookResp.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookResp.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.ownerId").value(bookResp.getItem().getOwnerId()))
                .andExpect(jsonPath("$.item.lastBooking").value(bookResp.getItem().getLastBooking()))
                .andExpect(jsonPath("$.item.nextBooking").value(bookResp.getItem().getNextBooking()))
                .andExpect(jsonPath("$.item.requestId").value(bookResp.getItem().getRequestId()))
                .andExpect(jsonPath("$.booker.id").value(bookResp.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookResp.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookResp.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookResp.getStatus().toString()));
    }

    @Test
    void approveBookingByForbiddenUserTest() throws Exception {
        boolean approve = true;
        when(bookingService.approve(1L, 1L, approve))
                .thenThrow(new ForbiddenUserException("Forbidden"));
        mvc.perform(patch("/bookings/{bookingId}?approved={approve}", 1L, approve)
                        .content(mapper.writeValueAsString(bookReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(1L,2L))
                .thenReturn(bookResp);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookResp.getId()))
                .andExpect(jsonPath("$.start").value(
                        bookResp.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(
                        bookResp.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.item.id").value(bookResp.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookResp.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookResp.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookResp.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.ownerId").value(bookResp.getItem().getOwnerId()))
                .andExpect(jsonPath("$.item.lastBooking").value(bookResp.getItem().getLastBooking()))
                .andExpect(jsonPath("$.item.nextBooking").value(bookResp.getItem().getNextBooking()))
                .andExpect(jsonPath("$.item.requestId").value(bookResp.getItem().getRequestId()))
                .andExpect(jsonPath("$.booker.id").value(bookResp.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookResp.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookResp.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookResp.getStatus().toString()));
    }

    @Test
    void getBookingsTest() throws Exception {
        State state = State.ALL;
        when(bookingService.getBookings(2L, state))
                .thenReturn(List.of(bookResp));
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookResp.getId()))
                .andExpect(jsonPath("$[0].start").value(
                        bookResp.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(
                        bookResp.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].item.id").value(bookResp.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookResp.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookResp.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookResp.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.ownerId").value(bookResp.getItem().getOwnerId()))
                .andExpect(jsonPath("$[0].item.lastBooking").value(bookResp.getItem().getLastBooking()))
                .andExpect(jsonPath("$[0].item.nextBooking").value(bookResp.getItem().getNextBooking()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookResp.getItem().getRequestId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookResp.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookResp.getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookResp.getBooker().getEmail()))
                .andExpect(jsonPath("$[0].status").value(bookResp.getStatus().toString()));
    }

    @Test
    void getCurrentUserBookingsTest() throws Exception {
        State state = State.ALL;
        when(bookingService.getCurrentUserBookings(2L, state))
                .thenReturn(List.of(bookResp));
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookResp.getId()))
                .andExpect(jsonPath("$[0].start").value(
                        bookResp.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(
                        bookResp.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].item.id").value(bookResp.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookResp.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookResp.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookResp.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.ownerId").value(bookResp.getItem().getOwnerId()))
                .andExpect(jsonPath("$[0].item.lastBooking").value(bookResp.getItem().getLastBooking()))
                .andExpect(jsonPath("$[0].item.nextBooking").value(bookResp.getItem().getNextBooking()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookResp.getItem().getRequestId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookResp.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookResp.getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookResp.getBooker().getEmail()))
                .andExpect(jsonPath("$[0].status").value(bookResp.getStatus().toString()));
    }
}
