package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    /*
    Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем, а затем подтверждён
    владельцем вещи. Эндпоинт — POST /bookings. После создания запрос находится в статусе WAITING —
    «ожидает подтверждения».
     */
    @PostMapping
    public BookingDtoResponse book(@RequestBody @Valid BookingDto bookingDto,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        bookingDto.setBookerId(userId);
        return bookingService.book(bookingDto);
    }

    /*
    Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи. Затем статус
    бронирования становится либо APPROVED, либо REJECTED. Эндпоинт — PATCH /bookings/{bookingId}?approved={approved},
    параметр approved может принимать значения true или false.
     */
    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@PathVariable long bookingId,
                              @RequestParam(required = true) boolean approved,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approve(bookingId, userId, approved);
    }

    /*
    Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
    либо владельцем вещи, к которой относится бронирование. Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId)  {
        return bookingService.getBookingById(bookingId, userId);
    }

    /*
    Получение списка всех бронирований текущего пользователя. Эндпоинт — GET /bookings?state={state}. Параметр state
    необязательный и по умолчанию равен ALL (англ. «все»). Также он может принимать значения CURRENT (англ. «текущие»),
    PAST (англ. «завершённые»), FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED
    (англ. «отклонённые»). Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookings(userId, state);
    }

    /*
    Получение списка бронирований для всех вещей текущего пользователя. Эндпоинт — GET /bookings/owner?state={state}.
    Этот запрос имеет смысл для владельца хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем
    сценарии.
     */
    @GetMapping("/owner")
    public List<BookingDtoResponse> getCurrentUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getCurrentUserBookings(userId, state);
    }
}
