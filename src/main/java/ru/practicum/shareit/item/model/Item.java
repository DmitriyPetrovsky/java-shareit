package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
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
    @Column(name = "request_id")
    private Long requestId;

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
