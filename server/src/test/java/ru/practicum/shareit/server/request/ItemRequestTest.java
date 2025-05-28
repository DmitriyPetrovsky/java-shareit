package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestTest {

    @Test
    void whenCreateItemRequestWithNoArgs_thenFieldsAreNull() {
        ItemRequest itemRequest = new ItemRequest();
        assertAll(
                () -> assertThat(itemRequest.getId()).isEqualTo(0L),
                () -> assertThat(itemRequest.getDescription()).isNull(),
                () -> assertThat(itemRequest.getRequestor()).isNull(),
                () -> assertThat(itemRequest.getCreated()).isNull(),
                () -> assertThat(itemRequest.getItems()).isNull()
        );
    }

    @Test
    void givenTwoEqualItemRequests_whenEquals_thenTrue() {
        User requestor = new User();
        requestor.setId(1L);
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Request 1");
        request1.setRequestor(requestor);
        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);
        request2.setDescription("Request 1");
        request2.setRequestor(requestor);
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void givenTwoDifferentItemRequests_whenEquals_thenFalse() {
        User requestor1 = new User();
        requestor1.setId(1L);
        User requestor2 = new User();
        requestor2.setId(2L);
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Request 1");
        request1.setRequestor(requestor1);
        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Request 2");
        request2.setRequestor(requestor2);
        assertThat(request1).isNotEqualTo(request2);
        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

}