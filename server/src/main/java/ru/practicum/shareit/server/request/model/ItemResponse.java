package ru.practicum.shareit.server.request.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {
    private long itemId;
    private String name;
    private long ownerId;
}
