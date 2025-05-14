package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {
    long id;
    @NotBlank
    @NotEmpty
    String description;
    User requestor;
    LocalDateTime created;
}
