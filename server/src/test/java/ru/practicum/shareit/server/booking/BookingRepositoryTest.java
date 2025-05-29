package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.server.booking.enums.Status.APPROVED;
import static ru.practicum.shareit.server.booking.enums.Status.REJECTED;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        em.persist(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        em.persist(booker);

        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        LocalDateTime now = LocalDateTime.now();

        pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(APPROVED);
        em.persist(pastBooking);

        currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(APPROVED);
        em.persist(currentBooking);

        futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(2));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(APPROVED);
        em.persist(futureBooking);

        rejectedBooking = new Booking();
        rejectedBooking.setStart(now.plusDays(3));
        rejectedBooking.setEnd(now.plusDays(4));
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(REJECTED);
        em.persist(rejectedBooking);

        em.flush();
    }

    @Test
    void findByBooker_Id_shouldReturnAllBookingsForBooker() {
        List<Booking> bookings = bookingRepository.findByBooker_Id(booker.getId());
        assertEquals(4, bookings.size());
        assertTrue(bookings.containsAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking)));
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc_shouldReturnAllBookingsForOwner() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId());
        assertEquals(4, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    @Test
    void existsByItem_IdAndBooker_IdAndEndBefore_shouldReturnTrueWhenBookingExists() {
        boolean exists = bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(
                item.getId(),
                booker.getId(),
                LocalDateTime.now());
        assertTrue(exists);
    }

    @Test
    void existsByItem_IdAndBooker_IdAndEndBefore_shouldReturnFalseWhenNoBooking() {
        boolean exists = bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(
                item.getId(),
                booker.getId(),
                LocalDateTime.now().minusDays(10));
        assertFalse(exists);
    }

    @Test
    void findLastBookingForItem_shouldReturnMostRecentPastBooking() {
        Optional<Booking> lastBooking = bookingRepository.findLastBookingForItem(
                item,
                LocalDateTime.now());
        assertTrue(lastBooking.isPresent());
        assertEquals(pastBooking.getId(), lastBooking.get().getId());
    }

    @Test
    void findLastBookingForItem_shouldReturnEmptyWhenNoPastBookings() {
        em.remove(pastBooking);
        em.remove(currentBooking);
        em.flush();
        Optional<Booking> lastBooking = bookingRepository.findLastBookingForItem(
                item,
                LocalDateTime.now());
        assertFalse(lastBooking.isPresent());
    }

    @Test
    void findNextBookingForItem_shouldReturnEarliestFutureBooking() {
        Optional<Booking> nextBooking = bookingRepository.findNextBookingForItem(
                item,
                LocalDateTime.now());
        assertTrue(nextBooking.isPresent());
        assertEquals(futureBooking.getId(), nextBooking.get().getId());
    }

    @Test
    void findNextBookingForItem_shouldNotReturnRejectedBookings() {
        Optional<Booking> nextBooking = bookingRepository.findNextBookingForItem(
                item,
                LocalDateTime.now());
        assertTrue(nextBooking.isPresent());
        assertNotEquals(rejectedBooking.getId(), nextBooking.get().getId());
    }

    @Test
    void findNextBookingForItem_shouldReturnEmptyWhenNoFutureBookings() {
        em.remove(futureBooking);
        em.remove(rejectedBooking);
        em.flush();
        Optional<Booking> nextBooking = bookingRepository.findNextBookingForItem(
                item,
                LocalDateTime.now());
        assertFalse(nextBooking.isPresent());
    }
}