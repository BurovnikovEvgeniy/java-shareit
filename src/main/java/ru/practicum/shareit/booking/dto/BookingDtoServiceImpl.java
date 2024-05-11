package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.exception.entity.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.bookingMapper;
import static ru.practicum.shareit.user.UserMapper.userMapper;


@Service
@RequiredArgsConstructor
public class BookingDtoServiceImpl implements BookingDtoService {
    private final BookingRepository bookingRepository;
    private final UserDtoService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoOut add(Long userId, BookingDto bookingDto) {
        User user = userMapper.toUser(userService.findById(userId));
        Optional<Item> itemById = itemRepository.findById(bookingDto.getItemId());
        if (itemById.isEmpty()) {
            throw new EntityNotFoundException("Вещь не найдена.");
        }
        Item item = itemById.get();
        bookingValidation(bookingDto, user, item);
        Booking booking = bookingMapper.toBooking(user, item, bookingDto);
        return bookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateOwnBookingDetails(userId, bookingId);
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return bookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut findBookingByUserId(Long userId, Long bookingId) {
        Optional<Booking> bookingById = bookingRepository.findById(bookingId);
        if (bookingById.isEmpty()) {
            throw new EntityNotFoundException("Бронь не найдена.");
        }
        Booking booking = bookingById.get();
        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), (userId))) {
            throw new EntityNotFoundException("Пользователь не владелец вещи и не автор бронирования");
        }
        return bookingMapper.toBookingOut(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAll(Long bookerId, String state, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        userService.findById(bookerId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(bookerId, pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(bookerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(bookerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(bookerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(bookerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(bookerId, pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAllOwner(Long ownerId, String state, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        userService.findById(ownerId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(ownerId, pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(ownerId, pageable).stream()
                        .map(bookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


    private void bookingValidation(BookingDto bookingDto, User user, Item item) {
        if (!item.getAvailable()) {
            throw new NotValidDataException("Вещь не доступена для бронирования.");
        }
        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new EntityNotFoundException("Вещь не найдена.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new NotValidDataException("Дата окончания не может быть раньше или равна дате начала");
        }
    }

    private BookingState validState(String bookingState) {
        BookingState state = BookingState.getState(bookingState);
        if (state == null) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return state;
    }

    private Booking validateOwnBookingDetails(Long userId, Long bookingId) {
        Optional<Booking> bookingById = bookingRepository.findById(bookingId);
        if (bookingById.isEmpty()) {
            throw new EntityNotFoundException("Бронь не найдена.");
        }
        Booking booking = bookingById.get();
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь не является владельцем");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new NotValidDataException("Бронь cо статусом не WAITING");
        }
        return booking;
    }
}