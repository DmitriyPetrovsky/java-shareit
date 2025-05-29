package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.State;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> book(@RequestBody @Valid BookingDto bookingDto,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        bookingDto.setBookerId(userId);
        log.info("Получен запрос на бронирование предмета ID: {} пользователем " +
                "с ID {} ", bookingDto.getItemId(), userId);
        return bookingClient.book(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на подтверждение бронирования ID {} пользователем ID {}", bookingId, userId);
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId)  {
        log.info("Получен запрос на данные о бронировании ID {} пользователем ID {}", bookingId, userId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(required = false, defaultValue = "ALL") State state,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на список бронирований пользователя ID: {}", userId);
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getCurrentUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") State state) {
        log.info("Получен запрос на список бронирования вещей пользователя ID {}", userId);
        return bookingClient.getCurrentUserBookings(userId, state);
    }
}
