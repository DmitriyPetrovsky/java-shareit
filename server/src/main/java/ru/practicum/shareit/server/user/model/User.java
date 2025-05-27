package ru.practicum.shareit.server.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (id == 0 && user.id == 0) {
            return Objects.equals(email, user.email);
        }
        return id == user.id;
    }

    @Override
    public int hashCode() {
        if (id == 0) {
            return Objects.hash(email);
        }
        return Objects.hash(id);
    }
}
