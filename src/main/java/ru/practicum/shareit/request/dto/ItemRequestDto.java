package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {
    long id;
    long requestorId;
    String description;
    List<ItemResponse> items;
    LocalDateTime created;
}
