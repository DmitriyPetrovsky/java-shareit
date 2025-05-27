package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);
        return mapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequests(long userId) {
        List<ItemRequest> requests = repository.findByRequestorId(userId);
        return requests.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(mapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        List<ItemRequest> requests = repository.findAll();
        return requests.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(mapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        ItemRequest ir = repository.findById(requestId).orElseThrow(() ->  new NotFoundException("Запрос не найден!"));
        return mapper.toItemRequestDto(ir);
    }
}
