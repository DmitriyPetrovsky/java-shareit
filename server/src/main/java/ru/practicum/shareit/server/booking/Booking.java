package ru.practicum.shareit.server.booking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking booking = (Booking) o;
        if (id == 0 && booking.id == 0) {
            return Objects.equals(start, booking.start) &&
                    Objects.equals(end, booking.end) &&
                    Objects.equals(item, booking.item) &&
                    Objects.equals(booker, booking.booker) &&
                    status == booking.status;
        }
        return id == booking.id;
    }

    @Override
    public int hashCode() {
        if (id == 0) {
            return Objects.hash(start, end, item, booker, status);
        }
        return Objects.hash(id);
    }
}
