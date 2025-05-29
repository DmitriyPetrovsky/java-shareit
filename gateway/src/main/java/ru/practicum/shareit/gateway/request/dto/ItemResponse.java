package ru.practicum.shareit.gateway.request.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {
    private long itemId;
    private String name;
    private long ownerId;
}
