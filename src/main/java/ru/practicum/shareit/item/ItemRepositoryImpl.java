package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final UserRepository userRepository;

    Map<Long, Item> items = new HashMap<>();
    private long id;

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item addItem(long userId, ItemDto itemDto) {
        userRepository.getById(userId);
        Item item = new Item();
        item.setId(getNextId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userRepository.getById(userId));
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto item) {
        userRepository.getById(userId);
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Не найден предмет с ID: " + itemId);
        }
        Item oldItem = items.get(itemId);
        if (oldItem.getOwner().getId() != userId) {
            throw new WrongUserException("Этот пользователь не может редактировать вещь с Id: " + itemId);
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, oldItem);
        return ItemMapper.toItemDto(oldItem);
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = items.get(id);
        if (item == null) {
            return null;
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(i -> i.isAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return id++;
    }
}
