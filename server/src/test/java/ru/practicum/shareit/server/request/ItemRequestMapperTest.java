package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.model.ItemResponse;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private User requestor;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("Requestor");
        requestor.setEmail("requestor@example.com");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setOwner(requestor);

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setOwner(requestor);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need something");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto(
                1L,
                1L,
                "Need something",
                List.of(),
                LocalDateTime.now()
        );
    }

    @Test
    void toItemRequestDto_ShouldMapCorrectly() {
        when(itemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(List.of(item1, item2));
        ItemRequestDto result = itemRequestMapper.toItemRequestDto(itemRequest);
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getRequestor().getId(), result.getRequestorId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertEquals(2, result.getItems().size());
        verify(itemRepository, times(1)).findItemsByRequestId(itemRequest.getId());
    }

    @Test
    void toItemRequestDto_WithNoItems_ShouldReturnEmptyItemsList() {
        when(itemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(List.of());
        ItemRequestDto result = itemRequestMapper.toItemRequestDto(itemRequest);
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void toItemRequest_ShouldMapCorrectly() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        ItemRequest result = itemRequestMapper.toItemRequest(itemRequestDto);
        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getRequestorId(), result.getRequestor().getId());
        verify(userRepository, times(1)).findById(itemRequestDto.getRequestorId());
    }

    @Test
    void toItemRequest_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                itemRequestMapper.toItemRequest(itemRequestDto)
        );
        verify(userRepository, times(1)).findById(itemRequestDto.getRequestorId());
    }

    @Test
    void getItemResponses_ShouldReturnCorrectResponses() {
        when(itemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(List.of(item1, item2));
        List<ItemResponse> result = itemRequestMapper.getItemResponses(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
        ItemResponse response1 = result.getFirst();
        assertEquals(item1.getId(), response1.getItemId());
        assertEquals(item1.getName(), response1.getName());
        assertEquals(item1.getOwner().getId(), response1.getOwnerId());
        ItemResponse response2 = result.get(1);
        assertEquals(item2.getId(), response2.getItemId());
        assertEquals(item2.getName(), response2.getName());
        assertEquals(item2.getOwner().getId(), response2.getOwnerId());
        verify(itemRepository, times(1)).findItemsByRequestId(1L);
    }

    @Test
    void getItemResponses_WhenNoItems_ShouldReturnEmptyList() {
        when(itemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(List.of());
        List<ItemResponse> result = itemRequestMapper.getItemResponses(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, times(1)).findItemsByRequestId(1L);
    }
}