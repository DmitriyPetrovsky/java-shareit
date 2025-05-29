package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.server.booking.enums.State;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.exception.*;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final LocalDateTime now = LocalDateTime.now();
    private final User owner = new User(1L, "Owner", "owner@mail.com");
    private final User booker = new User(2L, "Booker", "booker@mail.com");
    private final Item item = new Item(1L, "Item", "Description", true, owner, null, null, null, null);
    private final BookingDto bookingDto = new BookingDto(1L, now.plusHours(1), now.plusHours(2), 1L, 2L, Status.WAITING);

    @Test
    void book_shouldCreateBookingWhenAllConditionsMet() {
        assertNotNull(item.getOwner());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDtoResponse result = bookingService.book(bookingDto);
        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void book_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.book(bookingDto));
    }

    @Test
    void book_shouldThrowWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.book(bookingDto));
    }

    @Test
    void book_shouldThrowWhenItemUnavailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(UnavailableItemException.class, () -> bookingService.book(bookingDto));
    }

    @Test
    void book_shouldThrowWhenStartInPast() {
        BookingDto pastBooking = new BookingDto(1L, now.minusHours(1), now.plusHours(1), 1L, 2L, Status.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(WrongDateException.class, () -> bookingService.book(pastBooking));
    }

    @Test
    void approve_shouldApproveBooking() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDtoResponse result = bookingService.approve(1L, 1L, true);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approve_shouldRejectBooking() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDtoResponse result = bookingService.approve(1L, 1L, false);
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void approve_shouldThrowWhenNotOwner() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenUserException.class, () -> bookingService.approve(1L, 999L, true));
    }

    @Test
    void getBookingById_shouldReturnForOwner() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDtoResponse result = bookingService.getBookingById(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void getBookingById_shouldReturnForBooker() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDtoResponse result = bookingService.getBookingById(1L, 2L);
        assertNotNull(result);
    }

    @Test
    void getBookingById_shouldThrowForUnauthorizedUser() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenUserException.class, () -> bookingService.getBookingById(1L, 999L));
    }

    @Test
    void getBookings_shouldFilterByState() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_Id(anyLong())).thenReturn(List.of(booking));
        List<BookingDtoResponse> results = bookingService.getBookings(2L, State.WAITING);
        assertEquals(1, results.size());
    }

    @Test
    void getCurrentUserBookings_shouldFilterByState() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));
        List<BookingDtoResponse> results = bookingService.getCurrentUserBookings(1L, State.WAITING);
        assertEquals(1, results.size());
    }

    @Test
    void filterState_shouldHandleAllStates() {
        List<Booking> bookings = getBookings();
        assertEquals(4, bookingService.filterState(bookings, State.ALL).size());
        assertEquals(1, bookingService.filterState(bookings, State.CURRENT).size());
        assertEquals(1, bookingService.filterState(bookings, State.PAST).size());
        assertEquals(2, bookingService.filterState(bookings, State.FUTURE).size());
        assertEquals(1, bookingService.filterState(bookings, State.WAITING).size());
        assertEquals(1, bookingService.filterState(bookings, State.REJECTED).size());
    }

    private List<Booking> getBookings() {
        Booking current = new Booking(1L, now.minusHours(1), now.plusHours(1), item, booker, Status.APPROVED);
        Booking past = new Booking(2L, now.minusHours(2), now.minusHours(1), item, booker, Status.APPROVED);
        Booking future = new Booking(3L, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        Booking rejected = new Booking(4L, now.plusHours(3), now.plusHours(4), item, booker, Status.REJECTED);
        return List.of(current, past, future, rejected);
    }
}