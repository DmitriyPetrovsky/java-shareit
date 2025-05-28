package ru.practicum.shareit.server.user;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.user.model.User;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(0);
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1L, "John Doe", "john@example.com");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();

        user.setId(2L);
        user.setName("Jane Smith");
        user.setEmail("jane@example.com");

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getName()).isEqualTo("Jane Smith");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void testEqualsWithId() {
        User user1 = new User(1L, "User1", "user1@example.com");
        User user2 = new User(1L, "User2", "user2@example.com");
        User user3 = new User(2L, "User1", "user1@example.com");

        // Проверка равенства по id
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
    }

    @Test
    void testEqualsWithoutId() {
        User user1 = new User(0L, "User1", "user@example.com");
        User user2 = new User(0L, "User2", "user@example.com");
        User user3 = new User(0L, "User1", "another@example.com");

        // Проверка равенства по email, когда id = 0
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
    }

    @Test
    void testHashCodeWithId() {
        User user1 = new User(1L, "User1", "user1@example.com");
        User user2 = new User(1L, "User2", "user2@example.com");

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testHashCodeWithoutId() {
        User user1 = new User(0L, "User1", "user@example.com");
        User user2 = new User(0L, "User2", "user@example.com");
        User user3 = new User(0L, "User1", "another@example.com");

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
    }

    @Test
    void testEntityAnnotations() throws NoSuchFieldException {
        Entity entity = User.class.getAnnotation(Entity.class);
        Table table = User.class.getAnnotation(Table.class);
        Id id = User.class.getDeclaredField("id").getAnnotation(Id.class);
        GeneratedValue generatedValue = User.class.getDeclaredField("id").getAnnotation(GeneratedValue.class);

        assertThat(entity).isNotNull();
        assertThat(table).isNotNull();
        assertThat(table.name()).isEqualTo("users");
        assertThat(table.schema()).isEqualTo("public");
        assertThat(id).isNotNull();
        assertThat(generatedValue).isNotNull();
        assertThat(generatedValue.strategy()).isEqualTo(GenerationType.IDENTITY);
    }
}