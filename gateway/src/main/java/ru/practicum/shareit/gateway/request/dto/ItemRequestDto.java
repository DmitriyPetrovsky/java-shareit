package ru.practicum.shareit.gateway.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private long requestorId;
    @NotBlank
    private String description;
    private List<ItemResponse> items;
    private LocalDateTime created;
}
