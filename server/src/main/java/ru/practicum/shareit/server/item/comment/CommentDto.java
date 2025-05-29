package ru.practicum.shareit.server.item.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto {
    private long id;
    private String authorName;
    private String text;
    private LocalDateTime created;
}
