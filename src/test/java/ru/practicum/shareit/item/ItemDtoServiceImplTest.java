package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.ItemMapper.itemMapper;
import static ru.practicum.shareit.user.UserMapper.userMapper;

@ExtendWith(MockitoExtension.class)
public class ItemDtoServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserDtoService userService;

    @InjectMocks
    private ItemDtoServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("username2")
            .email("email2@email.com")
            .build();


    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemDtoOut itemDto = ItemDtoOut.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .comments(Collections.emptyList())
            .build();
    private final ItemDto itemDtoUpdate = ItemDto.builder()
            .build();

    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking lastBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .id(3L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(10L))
            .end(LocalDateTime.now().minusDays(9L))
            .build();

    private final Booking nextBooking = Booking.builder()
            .id(4L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .id(5L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(10L))
            .end(LocalDateTime.now().plusDays(20L))
            .build();

    @Test
    void getItemById() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemService.findById(user.getId(), item.getId());

        assertEquals(itemDto, actualItemDto);
    }


    @Test
    void updateItem() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userService.findById(user.getId())).thenReturn(userMapper.toUserDto(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));

        ItemDtoOut savedItem = itemService.update(user.getId(), itemDto.getId(), itemMapper.toItemDto(updatedItem));

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void updateItemWhenUserIsNotItemOwnerShouldThrowException() {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user2)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(updatedItem));
        when(userService.findById(user.getId())).thenReturn(userDto);

        NotValidDataException itemNotFoundException = assertThrows(NotValidDataException.class,
                () -> itemService.update(user.getId(), itemDto.getId(), itemMapper.toItemDto(updatedItem)));

        assertEquals(itemNotFoundException.getMessage(), "У пользователя с id = " + user.getId() +
                " не найдена вещь с id=" + item.getId());
    }

    @Test
    void updateItemWhenItemIdIsNotValid() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException itemNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.update(user.getId(), itemDto.getId(), itemMapper.toItemDto(item)));
        assertEquals(itemNotFoundException.getMessage(), "Вещи с " + item.getId() + " не существует");
    }


    @Test
    void getAllComments() {
        List<CommentDtoOut> expectedCommentsDto = List.of(CommentMapper.toCommentDtoOut(comment));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<CommentDtoOut> actualComments = commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(Collectors.toList());

        assertEquals(actualComments.size(), 1);
        assertEquals(actualComments, expectedCommentsDto);
    }

    @Test
    void searchItems() {
        Page<Item> items = new PageImpl<>(List.of(item));
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDtoOut> actualItemsDto = itemService.findAllByUserId(1L, 0, 10);

        assertEquals(1, actualItemsDto.size());
        assertEquals(1, actualItemsDto.get(0).getId());
        assertEquals("item name", actualItemsDto.get(0).getName());
    }

    @Test
    void createComment() {
        CommentDtoOut expectedCommentDto = CommentMapper.toCommentDtoOut(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoOut actualCommentDto = itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId());

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void createComment_whenItemIdIsNotValid_thenThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        EntityNotFoundException itemNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(itemNotFoundException.getMessage(), "У пользователя с id=" + user.getId() + " не существует вещи с id=" + item.getId());
    }

    @Test
    void createCommentWhenUserHaveNotAnyBookingsShouldThrowValidationException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        NotValidDataException userBookingsNotFoundException = assertThrows(NotValidDataException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(userBookingsNotFoundException.getMessage(), "У пользователя с id=" + user.getId() + " должно быть хотя бы одно бронирование предмета с id=" + item.getId());
    }
}
