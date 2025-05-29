package ru.practicum.shareit.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDto {
    private long id;
    private String authorName;
    private String text;
    private LocalDateTime created;
}
