package ru.practicum.shareit.server.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.item.model.Item;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    boolean existsByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item = :item
            AND b.end < :currentTime
            AND b.status = 'APPROVED'
            ORDER BY b.end DESC
            LIMIT 1
            """)
    Optional<Booking> findLastBookingForItem(
            @Param("item") Item item,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item = :item
            AND b.start > :currentTime
            AND b.status = 'APPROVED'
            ORDER BY b.start ASC
            LIMIT 1
            """)
    Optional<Booking> findNextBookingForItem(
            @Param("item") Item item,
            @Param("currentTime") LocalDateTime currentTime);

}
