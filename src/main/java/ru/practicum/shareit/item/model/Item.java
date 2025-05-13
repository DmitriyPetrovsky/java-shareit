package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Entity
@Data
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
}
