package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoResponse book(BookingDto bookingDto) {
        User booker = userRepository.findById(bookingDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        LocalDateTime now = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(now)) {
            throw new WrongDateException("Время начала не может быть в прошлом");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new WrongDateException("Время начала не может быть позже окончания");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new WrongDateException("Время начала не может совпадать с окончанием");
        }
        if (!item.isAvailable()) {
            throw new UnavailableItemException(
                    String.format("Предмет %s с ID %d не доступен для бронирования", item.getName(), item.getId()));
        }
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        Booking result = bookingRepository.save(booking);
        return toBookingDtoResponse(result);
    }

    public BookingDtoResponse approve(long bookingId, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        User owner = booking.getItem().getOwner();
        if (owner.getId() != userId) {
            throw new ForbiddenUserException(String.format("Пользователь с ID %d не может утверждать " +
                    "бронирование с ID %d", userId, bookingId));
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking result = bookingRepository.save(booking);
        return toBookingDtoResponse(result);
    }

    public BookingDtoResponse getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (userId == ownerId || userId == bookerId) {
            return toBookingDtoResponse(booking);
        }
        throw new ForbiddenUserException("Этот пользователь не может просматривать запрашиваемое бронирование");
    }

    public List<BookingDtoResponse> getBookings(long userId, State state) {
        checkUserExists(userId);
        List<Booking> bookings = bookingRepository.findByBooker_Id(userId);
        return filterState(bookings, state);
    }

    public List<BookingDtoResponse> getCurrentUserBookings(long userId, State state) {
        checkUserExists(userId);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        return filterState(bookings, state);
    }


    private List<BookingDtoResponse> filterState(List<Booking> bookings, State state) {
        LocalDateTime now = LocalDateTime.now();

        Predicate<Booking> filter = switch (state) {
            case ALL -> b -> true;
            case CURRENT -> b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
            case PAST -> b -> b.getEnd().isBefore(now);
            case FUTURE -> b -> b.getStart().isAfter(now);
            case WAITING -> b -> b.getStatus() == Status.WAITING;
            case REJECTED -> b -> b.getStatus() == Status.REJECTED;
        };

        return bookings.stream()
                .filter(filter)
                .map(this::toBookingDtoResponse)
                .sorted(Comparator.comparing(BookingDtoResponse::getStart))
                .collect(Collectors.toList());
    }

    private BookingDtoResponse toBookingDtoResponse(Booking booking) {
        BookingDtoResponse bdr = new BookingDtoResponse();
        bdr.setId(booking.getId());
        bdr.setStart(booking.getStart());
        bdr.setEnd(booking.getEnd());
        bdr.setStatus(booking.getStatus());
        bdr.setItem(ItemMapper.toItemDto(booking.getItem()));
        bdr.setBooker(UserMapper.toUserDto(booking.getBooker()));
        return bdr;
    }

    private User checkUserExists(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
