package ru.practicum.shareit.server.item.comment;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "created_at")
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        if (id == 0 && comment.id == 0) {
            return Objects.equals(text, comment.text) &&
                    Objects.equals(author, comment.author) &&
                    Objects.equals(item, comment.item) &&
                    Objects.equals(created, comment.created);
        }
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        if (id == 0) {
            return Objects.hash(text, author, item, created);
        }
        return Objects.hash(id);
    }
}
