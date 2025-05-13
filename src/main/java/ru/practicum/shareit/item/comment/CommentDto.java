package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDto {
    private long id;
    private String authorName;
    @NotNull
    private String text;
    private LocalDateTime created;
}
