package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequests(long requestorId);

    List<ItemRequestDto> getAllRequests(long requestorId);

    ItemRequestDto findById(long userId, long requestId);

}
