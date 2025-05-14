package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;

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
