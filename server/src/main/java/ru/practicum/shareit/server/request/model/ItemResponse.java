package ru.practicum.shareit.server.request.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ItemResponse {
    private long itemId;
    private String name;
    private long ownerId;
}
