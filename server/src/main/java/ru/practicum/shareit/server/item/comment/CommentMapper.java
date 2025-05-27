package ru.practicum.shareit.server.item.comment;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getAuthor().getName(), comment.getText(), comment.getCreated());
    }

    public static List<CommentDto> toDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }
}
