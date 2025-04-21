package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {

    List<ItemDto> getItemsByUserId(long userId);

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    ItemDto getItemById(long id);

    List<ItemDto> searchItems(String text);

}
