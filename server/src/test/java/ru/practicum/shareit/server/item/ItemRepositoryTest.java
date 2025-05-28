package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner1;
    private User owner2;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemRequest request; // Добавляем поле для запроса

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        owner1 = new User();
        owner1.setName("Owner 1");
        owner1.setEmail("owner1@example.com");
        em.persist(owner1);

        owner2 = new User();
        owner2.setName("Owner 2");
        owner2.setEmail("owner2@example.com");
        em.persist(owner2);

        request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(owner1);
        request.setCreated(LocalDateTime.now());
        em.persist(request);

        item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Аккумуляторная дрель");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        item1.setRequestId(request.getId());
        em.persist(item1);

        item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Набор отверток");
        item2.setAvailable(true);
        item2.setOwner(owner1);
        em.persist(item2);

        item3 = new Item();
        item3.setName("Молоток");
        item3.setDescription("Строительный молоток");
        item3.setAvailable(false);
        item3.setOwner(owner2);
        em.persist(item3);

        em.flush();
    }

    @Test
    void findItemsByOwnerId_shouldReturnItemsForOwner() {
        List<Item> items = itemRepository.findItemsByOwner_Id(owner1.getId());
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
        assertFalse(items.contains(item3));
    }

    @Test
    void findItemsByOwnerId_shouldReturnEmptyListForNonExistingOwner() {
        List<Item> items = itemRepository.findItemsByOwner_Id(999L);
        assertTrue(items.isEmpty());
    }

    @Test
    void search_shouldReturnEmptyListForBlankText() {
        List<Item> result = itemRepository.search("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void search_shouldReturnEmptyListForNonMatchingText() {
        List<Item> result = itemRepository.search("несуществующий текст");
        assertTrue(result.isEmpty());
    }

    @Test
    void findItemsByRequestId_shouldReturnItemsWithRequestId() {
        assertNotNull(request.getId());
        assertNotNull(item1.getRequestId());
        assertEquals(request.getId(), item1.getRequestId());
        List<Item> items = itemRepository.findItemsByRequestId(request.getId());
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
    }

    @Test
    void findItemsByRequestId_shouldReturnEmptyListForNonExistingRequestId() {
        List<Item> items = itemRepository.findItemsByRequestId(999L);
        assertTrue(items.isEmpty());
    }
}