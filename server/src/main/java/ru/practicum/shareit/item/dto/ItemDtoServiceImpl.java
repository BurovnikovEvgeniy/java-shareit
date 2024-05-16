package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.bookingMapper;
import static ru.practicum.shareit.item.ItemMapper.itemMapper;
import static ru.practicum.shareit.user.UserMapper.userMapper;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemDtoServiceImpl implements ItemDtoService {

    private final ItemRepository itemStorage;
    private final UserDtoService userDtoService;
    private final CommentRepository commentStorage;
    private final BookingRepository bookingStorage;
    private final ItemRequestRepository itemRequestStorage;

    @Override
    @Transactional
    public ItemDtoOut add(Long userId, ItemDto itemDto) {
        UserDto user = userDtoService.findById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner((userMapper.toUser(user)));
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestStorage.getReferenceById(itemDto.getRequestId()));
        }
        return itemMapper.toItemDtoOut(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto) {
        UserDto ownerDto = userDtoService.findById(userId);
        Item resItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещи с " + itemId + " не существует"));
        if (resItem.getOwner().getId() != ownerDto.getId()) {
            throw new NotValidDataException("У пользователя с id = " + userId + " не найдена вещь с id=" + itemId);
        }
        if (itemDto.getName() != null) {
            resItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            resItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            resItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDtoOut(resItem);
    }

    @Override
    @Transactional
    public ItemDtoOut findById(Long userId, Long itemId) {
        userDtoService.findById(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещи с " + itemId + " не существует"));
        ItemDtoOut itemDtoOut = itemMapper.toItemDtoOut(item);
        itemDtoOut.setComments(getAllItemComments(itemId));
        if (!Objects.equals(item.getOwner().getId(), (userId))) {
            return itemDtoOut;
        }
        List<Booking> bookings = bookingStorage.findAllByItemAndStatusOrderByStartAsc(item, BookingStatus.APPROVED);
        List<BookingDtoOut> bookingDTOList = bookings
                .stream()
                .map(bookingMapper::toBookingOut)
                .collect(toList());

        itemDtoOut.setLastBooking(getLastBooking(bookingDTOList, LocalDateTime.now()));
        itemDtoOut.setNextBooking(getNextBooking(bookingDTOList, LocalDateTime.now()));
        return itemDtoOut;
    }

    @Override
    public List<ItemDtoOut> findAllByUserId(Long userId, Integer from, Integer size) {
        userDtoService.findById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> itemList = itemStorage.findAllByOwnerId(userId, pageable);
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, List<CommentDtoOut>> comments = commentStorage.findAllByItemIdIn(idList)
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));
        Map<Long, List<BookingDtoOut>> bookings = bookingStorage.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        BookingStatus.APPROVED)
                .stream()
                .map(bookingMapper::toBookingOut)
                .collect(groupingBy(BookingDtoOut::getItemId, toList()));
        return itemList.stream()
                .map(item -> itemMapper.toItemDtoOut(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> search(Long userId, String text, Integer from, Integer size) {
        userDtoService.findById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.search(text, pageable)
                .stream().map(itemMapper::toItemDtoOut)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userMapper.toUser(userDtoService.findById(userId));
        Optional<Item> itemById = itemStorage.findById(itemId);

        if (itemById.isEmpty()) {

            throw new EntityNotFoundException("У пользователя с id=" + userId + " не " +
                    "существует вещи с id=" + itemId);
        }
        Item item = itemById.get();

        List<Booking> userBookings = bookingStorage.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new NotValidDataException("У пользователя с id=" + userId + " должно быть хотя бы одно бронирование предмета с id=" + itemId);
        }

        return CommentMapper.toCommentDtoOut(commentStorage.save(CommentMapper.toComment(commentDto, item, user)));
    }

    private List<CommentDtoOut> getAllItemComments(Long itemId) {
        List<Comment> comments = commentStorage.findAllByItemId(itemId);
        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(Collectors.toList());
    }

    private BookingDtoOut getLastBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings
                .stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingDtoOut getNextBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}
