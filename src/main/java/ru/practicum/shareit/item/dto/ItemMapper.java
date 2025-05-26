package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner().getId(),
                item.getLastBooking() != null ? BookingMapper.toBookingDto(item.getLastBooking()) : null,
                item.getNextBooking() != null ? BookingMapper.toBookingDto(item.getNextBooking()) : null,
                item.getComments() != null ? CommentMapper.toDtoList(item.getComments()) : null,
                item.getRequestId()
        );
        return itemDto;
    }

}
