package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final User user1 = new User(1L, "User1", "user1@mail.com");
    private final User user2 = new User(2L, "User2", "user2@mail.com");
    private final Item item1 = new Item(1L, "Item1", "Description1", true, user1, null, null, null, null);
    private final Item item2 = new Item(2L, "Item2", "Description2", true, user2, null, null, null, null);

    @Test
    void testEqualsAndHashCode() {
        Booking booking1 = new Booking(1L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        Booking booking2 = new Booking(1L, now.plusHours(3), now.plusHours(4), item2, user2, Status.APPROVED);
        Booking booking3 = new Booking(2L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1, booking3);
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
        Booking booking4 = new Booking(0L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        Booking booking5 = new Booking(0L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        Booking booking6 = new Booking(0L, now.plusHours(3), now.plusHours(4), item2, user2, Status.APPROVED);
        assertEquals(booking4, booking5);
        assertEquals(booking4.hashCode(), booking5.hashCode());
        assertNotEquals(booking4, booking6);
    }

    @Test
    void testEqualsWithNull() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        assertNotEquals(null, booking);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        assertNotEquals(booking, new Object());
    }

    @Test
    void testGettersAndSetters() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setStatus(Status.WAITING);
        assertEquals(1L, booking.getId());
        assertEquals(now.plusHours(1), booking.getStart());
        assertEquals(now.plusHours(2), booking.getEnd());
        assertEquals(item1, booking.getItem());
        assertEquals(user1, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        assertEquals(1L, booking.getId());
        assertEquals(now.plusHours(1), booking.getStart());
        assertEquals(now.plusHours(2), booking.getEnd());
        assertEquals(item1, booking.getItem());
        assertEquals(user1, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        Booking booking = new Booking();
        assertNotNull(booking);
    }

    @Test
    void testToString() {
        Booking booking = new Booking(1L, now.plusHours(1), now.plusHours(2), item1, user1, Status.WAITING);
        assertNotNull(booking.toString());
        assertTrue(booking.toString().contains("Booking"));
    }
}