package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.item.comment.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private User author;
    private Item item;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setName("John Doe");
        author.setEmail("john@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);

        comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Great item!");
        comment1.setAuthor(author);
        comment1.setItem(item);

        comment2 = new Comment();
        comment2.setId(1L);
        comment2.setText("Great item!");
        comment2.setAuthor(author);
        comment2.setItem(item);

        comment3 = new Comment();
        comment3.setId(2L);
        comment3.setText("Not so good");
        comment3.setAuthor(author);
        comment3.setItem(item);
    }

    @Test
    void testEquals_SameObject_ReturnsTrue() {
        assertEquals(comment1, comment1);
    }

    @Test
    void testEquals_NullObject_ReturnsFalse() {
        assertNotEquals(null, comment1);
    }

    @Test
    void testEquals_DifferentClass_ReturnsFalse() {
        assertNotEquals(new Object(), comment1);
    }

    @Test
    void testEquals_EqualCommentsWithIds_ReturnsTrue() {
        assertEquals(comment1, comment2);
    }

    @Test
    void testEquals_DifferentIds_ReturnsFalse() {
        assertNotEquals(comment1, comment3);
    }

    @Test
    void testEquals_NewCommentsWithSameContent_ReturnsTrue() {
        Comment newComment1 = new Comment();
        newComment1.setText("Test comment");
        newComment1.setAuthor(author);
        newComment1.setItem(item);
        Comment newComment2 = new Comment();
        newComment2.setText("Test comment");
        newComment2.setAuthor(author);
        newComment2.setItem(item);
        assertEquals(newComment1, newComment2);
    }

    @Test
    void testEquals_NewCommentsWithDifferentContent_ReturnsFalse() {
        Comment newComment1 = new Comment();
        newComment1.setText("Test comment 1");
        newComment1.setAuthor(author);
        newComment1.setItem(item);
        Comment newComment2 = new Comment();
        newComment2.setText("Test comment 2");
        newComment2.setAuthor(author);
        newComment2.setItem(item);
        assertNotEquals(newComment1, newComment2);
    }

    @Test
    void testHashCode_EqualObjects_SameHashCode() {
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void testHashCode_DifferentObjects_DifferentHashCode() {
        assertNotEquals(comment1.hashCode(), comment3.hashCode());
    }

    @Test
    void testHashCode_NewCommentsWithSameContent_SameHashCode() {
        Comment newComment1 = new Comment();
        newComment1.setText("Test comment");
        newComment1.setAuthor(author);
        newComment1.setItem(item);
        Comment newComment2 = new Comment();
        newComment2.setText("Test comment");
        newComment2.setAuthor(author);
        newComment2.setItem(item);
        assertEquals(newComment1.hashCode(), newComment2.hashCode());
    }

    @Test
    void testPrePersist_SetsCreatedTimestamp() {
        Comment newComment = new Comment();
        assertNull(newComment.getCreated());
        newComment.onCreate();
        assertNotNull(newComment.getCreated());
        assertTrue(newComment.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                newComment.getCreated().isEqual(LocalDateTime.now()));
    }

    @Test
    void testGettersAndSetters() {
        Comment comment = new Comment();
        comment.setId(5L);
        comment.setText("Test text");
        comment.setAuthor(author);
        comment.setItem(item);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);
        assertEquals(5L, comment.getId());
        assertEquals("Test text", comment.getText());
        assertEquals(author, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertEquals(now, comment.getCreated());
    }
}