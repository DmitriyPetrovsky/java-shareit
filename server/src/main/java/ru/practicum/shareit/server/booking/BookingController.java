package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.server.booking.enums.State;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse book(@RequestBody BookingDto bookingDto,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        bookingDto.setBookerId(userId);
        log.info("Получен запрос на бронирование предмета ID: {} пользователем " +
                "с ID {} ", bookingDto.getItemId(), userId);
        return bookingService.book(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@PathVariable long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на подтверждение бронирования ID {} пользователем ID {}", bookingId, userId);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId)  {
        log.info("Получен запрос на данные о бронировании ID {} пользователем ID {}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestParam(required = false, defaultValue = "ALL") State state,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на список бронирований пользователя ID: {}", userId);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getCurrentUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") State state) {
        log.info("Получен запрос на список бронирования вещей пользователя ID {}", userId);
        return bookingService.getCurrentUserBookings(userId, state);
    }
}
