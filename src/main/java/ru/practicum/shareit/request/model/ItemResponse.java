package ru.practicum.shareit.request.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {
    private long itemId;
    private String name;
    private long ownerId;
}
