package ru.practicum.shareit.server.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.item.comment.Comment;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Comment> comments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        if (id == 0 && item.id == 0) {
            return available == item.available &&
                    Objects.equals(name, item.name) &&
                    Objects.equals(description, item.description) &&
                    Objects.equals(owner, item.owner);
        }
        return id == item.id;
    }

    @Override
    public int hashCode() {
        if (id == 0) {
            return Objects.hash(name, description, available, owner);
        }
        return Objects.hash(id);
    }
}
