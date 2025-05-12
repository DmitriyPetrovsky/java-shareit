package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class CommentDTO {
    long id;
    String text;
    UserDto author;
    ItemDto item;
}
