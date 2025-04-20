package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    private long id;
    @NotBlank
    @NotEmpty
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
