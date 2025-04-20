package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return itemRepository.getItemsByUserId(userId);
    }

    @Override
    public Item createItem(long userId, ItemDto itemDto) {
        return itemRepository.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        return itemRepository.updateItem(itemId, userId, itemDto);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItems(text);
    }

}
