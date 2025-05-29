package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.request.model.ItemResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ItemResponseTest {

    @Test
    void testEqualsAndHashCode() {
        ItemResponse response1 = new ItemResponse();
        response1.setItemId(1L);
        response1.setName("Item 1");
        response1.setOwnerId(10L);

        ItemResponse response2 = new ItemResponse();
        response2.setItemId(1L);
        response2.setName("Item 1");
        response2.setOwnerId(10L);

        ItemResponse response3 = new ItemResponse();
        response3.setItemId(2L);
        response3.setName("Item 2");
        response3.setOwnerId(20L);

        // Проверка equals
        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(response3);

        // Проверка hashCode
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        assertThat(response1.hashCode()).isNotEqualTo(response3.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        ItemResponse response = new ItemResponse();

        // Установка значений через сеттеры
        response.setItemId(5L);
        response.setName("Test Item");
        response.setOwnerId(15L);

        // Проверка значений через геттеры
        assertThat(response.getItemId()).isEqualTo(5L);
        assertThat(response.getName()).isEqualTo("Test Item");
        assertThat(response.getOwnerId()).isEqualTo(15L);
    }

    @Test
    void testNoArgsConstructor() {
        ItemResponse response = new ItemResponse();

        // Проверяем, что объект создан и поля инициализированы значениями по умолчанию
        assertThat(response).isNotNull();
        assertThat(response.getItemId()).isEqualTo(0L);
        assertThat(response.getName()).isNull();
        assertThat(response.getOwnerId()).isEqualTo(0L);
    }
}