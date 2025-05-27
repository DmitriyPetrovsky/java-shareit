package ru.practicum.shareit.server.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.item.dto.ItemMapper;


@Component
@RequiredArgsConstructor
@Data
public class BookingMapper {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

}
