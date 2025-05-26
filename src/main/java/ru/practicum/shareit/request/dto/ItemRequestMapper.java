package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getRequestor().getId(),
                itemRequest.getDescription(),
                getItemResponses(itemRequest.getId()),
                itemRequest.getCreated());
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(userRepository.findById(itemRequestDto.getRequestorId())
                .orElseThrow( () -> new NotFoundException("Пользователь не найден")));
        return itemRequest;
    }

    public List<ItemResponse> getItemResponses(Long requestId) {
        List<Item> items = itemRepository.findItemsByRequestId(requestId);
        List<ItemResponse> itemResponses = new ArrayList<>();
        items.forEach(item -> {
            ItemResponse itemResponse = new ItemResponse();
            itemResponse.setItemId(item.getId());
            itemResponse.setName(item.getName());
            itemResponse.setOwnerId(item.getOwner().getId());
            itemResponses.add(itemResponse);
        });
        return itemResponses;
    }
}
