package ru.practicum.shareit.server.request;



import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequests(long requestorId);

    List<ItemRequestDto> getAllRequests(long requestorId);

    ItemRequestDto findById(long userId, long requestId);

}
