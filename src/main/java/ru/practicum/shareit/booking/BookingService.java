package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse book(BookingDto bookingDto);
    BookingDtoResponse approve(long bookingId, long userId, boolean approved);
    BookingDtoResponse getBookingById(long bookingId, long userId);
    List<BookingDtoResponse> getBookings(long userId, String state);
    List<BookingDtoResponse> getCurrentUserBookings(long userId, String state);
}
