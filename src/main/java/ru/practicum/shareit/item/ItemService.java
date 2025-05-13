package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItemsByUserId(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> searchItem(String text);

    CommentDto postComment(CommentDto commentDto, long userId, long itemId);


}
