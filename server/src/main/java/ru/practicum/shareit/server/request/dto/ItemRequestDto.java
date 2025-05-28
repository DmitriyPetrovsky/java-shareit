package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.request.model.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {
    long id;
    long requestorId;
    String description;
    List<ItemResponse> items;
    LocalDateTime created;
}
