package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUserId(long userId);
    Item createItem(long userId, ItemDto itemDto);
    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);
    ItemDto getItemById(long itemId);
    List<ItemDto> searchItem(String text);
}
