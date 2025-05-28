package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.exception.BadRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.comment.Comment;
import ru.practicum.shareit.server.item.comment.CommentDto;
import ru.practicum.shareit.server.item.comment.CommentRepository;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private ItemDto itemDto;
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(owner.getId());

        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setItem(item);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment text");
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setText("Comment text");
    }

    @Test
    void getItemsByUserId_shouldReturnListOfItems() {
        when(itemRepository.findItemsByOwner_Id(anyLong())).thenReturn(List.of(item));
        List<ItemDto> result = itemService.getItemsByUserId(owner.getId());
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.getFirst().getId());
        verify(itemRepository, times(1)).findItemsByOwner_Id(owner.getId());
    }

    @Test
    void createItem_shouldCreateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto result = itemService.createItem(owner.getId(), itemDto);
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.createItem(999L, itemDto));
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto updatedDto = new ItemDto();
        updatedDto.setName("Updated name");
        updatedDto.setDescription("Updated description");
        updatedDto.setAvailable(false);
        ItemDto result = itemService.updateItem(item.getId(), owner.getId(), updatedDto);
        assertNotNull(result);
        assertEquals("Updated name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertFalse(result.getAvailable());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(999L, owner.getId(), itemDto));
        verify(itemRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenUserNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item.getId(), 999L, itemDto));
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemById_shouldReturnItemWithoutBookingsForNonOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItem_Id(anyLong())).thenReturn(Collections.emptyList());
        ItemDto result = itemService.getItemById(item.getId(), booker.getId());
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).findLastBookingForItem(any(Item.class), any(LocalDateTime.class));
        verify(bookingRepository, never()).findNextBookingForItem(any(Item.class), any(LocalDateTime.class));
    }

    @Test
    void getItemById_shouldThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999L, owner.getId()));
        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    void searchItem_shouldReturnEmptyListWhenTextIsBlank() {
        List<ItemDto> result = itemService.searchItem("   ");
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void searchItem_shouldReturnFilteredItems() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));
        List<ItemDto> result = itemService.searchItem("test");
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.getFirst().getId());
        verify(itemRepository, times(1)).search("test");
    }

    @Test
    void postComment_shouldThrowExceptionWhenUserDidNotBookItem() {
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(
                eq(item.getId()),
                eq(booker.getId()),
                any(LocalDateTime.class)))
                .thenReturn(false);
        assertThrows(BadRequestException.class,
                () -> itemService.postComment(commentDto, booker.getId(), item.getId()));
        verify(bookingRepository, times(1))
                .existsByItem_IdAndBooker_IdAndEndBefore(
                        eq(item.getId()),
                        eq(booker.getId()),
                        any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void postComment_shouldThrowExceptionWhenUserNotFound() {
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.postComment(commentDto, 999L, item.getId()));
        verify(userRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void postComment_shouldThrowExceptionWhenItemNotFound() {
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.postComment(commentDto, booker.getId(), 999L));
        verify(itemRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }
}