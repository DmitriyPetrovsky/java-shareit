package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        List<Item> items = itemRepository.findItemsByOwner_Id(userId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        itemDto.setOwnerId(userId);
        return ItemMapper.toItemDto(
                itemRepository.save(dtoToItem(itemDto))
        );
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден!"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException(String.format(
                    "Пользователь с ID %d не может изменять предмет с ID %d", itemId, userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден!"));
        List<Comment> comments = commentRepository.findByItem_Id(itemId);
        item.setComments(comments);
        if (item.getOwner().getId() == userId) {
            Booking lastBooking = bookingRepository.findLastBookingForItem(item, LocalDateTime.now()).orElse(null);
            Booking nextBooking = bookingRepository.findNextBookingForItem(item, LocalDateTime.now()).orElse(null);
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.search(text);
        return items.stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto postComment(CommentDto commentDto, long userId, long itemId) {
        if (!bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(itemId, userId, LocalDateTime.now())) {
            throw new BadRequestException("Пользователь не может комментировать этот элемент");
        }
        Comment comment = dtoToComment(commentDto, userId, itemId);
        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }


    private Item dtoToItem(ItemDto itemDto) {
        Item item = new Item();
        User user = userRepository.findById(itemDto.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + itemDto.getOwnerId()
                        + " не найден!"));
        item.setOwner(user);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    private Comment dtoToComment(CommentDto dto, long userId, long itemId) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден")));
        return comment;
    }

}
