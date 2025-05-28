package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto createdItemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need something");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(
                1L,
                1L,
                "Need something",
                List.of(),
                LocalDateTime.now()
        );

        createdItemRequestDto = new ItemRequestDto(
                1L,
                1L,
                "Need something",
                List.of(),
                LocalDateTime.now()
        );
    }

    @Test
    void createItemRequest_ShouldSaveAndReturnDto() {
        when(mapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(repository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(mapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(createdItemRequestDto);
        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestDto);
        assertNotNull(result);
        assertEquals(createdItemRequestDto, result);
        verify(mapper, times(1)).toItemRequest(itemRequestDto);
        verify(repository, times(1)).save(itemRequest);
        verify(mapper, times(1)).toItemRequestDto(itemRequest);
    }

    @Test
    void getRequests_ShouldReturnSortedListOfUserRequests() {
        ItemRequest olderRequest = new ItemRequest();
        olderRequest.setId(2L);
        olderRequest.setCreated(LocalDateTime.now().minusDays(1));
        ItemRequest newerRequest = new ItemRequest();
        newerRequest.setId(3L);
        newerRequest.setCreated(LocalDateTime.now());
        List<ItemRequest> requests = List.of(olderRequest, newerRequest);
        List<ItemRequestDto> expectedDtos = List.of(
                new ItemRequestDto(3L, 1L, "Newer", List.of(), newerRequest.getCreated()),
                new ItemRequestDto(2L, 1L, "Older", List.of(), olderRequest.getCreated())
        );
        when(repository.findByRequestorId(anyLong())).thenReturn(requests);
        when(mapper.toItemRequestDto(newerRequest)).thenReturn(expectedDtos.get(0));
        when(mapper.toItemRequestDto(olderRequest)).thenReturn(expectedDtos.get(1));
        List<ItemRequestDto> result = itemRequestService.getRequests(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        assertTrue(result.get(0).getCreated().isAfter(result.get(1).getCreated()));
        verify(repository, times(1)).findByRequestorId(1L);
        verify(mapper, times(1)).toItemRequestDto(newerRequest);
        verify(mapper, times(1)).toItemRequestDto(olderRequest);
    }

    @Test
    void getRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(repository.findByRequestorId(anyLong())).thenReturn(List.of());
        List<ItemRequestDto> result = itemRequestService.getRequests(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByRequestorId(1L);
        verify(mapper, never()).toItemRequestDto(any());
    }

    @Test
    void getAllRequests_ShouldReturnSortedListOfAllRequests() {
        ItemRequest olderRequest = new ItemRequest();
        olderRequest.setId(2L);
        olderRequest.setCreated(LocalDateTime.now().minusDays(1));
        ItemRequest newerRequest = new ItemRequest();
        newerRequest.setId(3L);
        newerRequest.setCreated(LocalDateTime.now());
        List<ItemRequest> requests = List.of(olderRequest, newerRequest);
        List<ItemRequestDto> expectedDtos = List.of(
                new ItemRequestDto(3L, 1L, "Newer", List.of(), newerRequest.getCreated()),
                new ItemRequestDto(2L, 1L, "Older", List.of(), olderRequest.getCreated())
        );
        when(repository.findAll()).thenReturn(requests);
        when(mapper.toItemRequestDto(newerRequest)).thenReturn(expectedDtos.get(0));
        when(mapper.toItemRequestDto(olderRequest)).thenReturn(expectedDtos.get(1));
        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        assertTrue(result.get(0).getCreated().isAfter(result.get(1).getCreated()));
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toItemRequestDto(newerRequest);
        verify(mapper, times(1)).toItemRequestDto(olderRequest);
    }

    @Test
    void getAllRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());
        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
        verify(mapper, never()).toItemRequestDto(any());
    }

    @Test
    void findById_WhenRequestExists_ShouldReturnRequestDto() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(mapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);
        ItemRequestDto result = itemRequestService.findById(1L, 1L);
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).toItemRequestDto(itemRequest);
    }

    @Test
    void findById_WhenRequestNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                itemRequestService.findById(1L, 1L)
        );
        verify(repository, times(1)).findById(1L);
        verify(mapper, never()).toItemRequestDto(any());
    }
}