package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.exception.entity.NotValidDataException;
import ru.practicum.shareit.exception.entity.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingMapper.bookingMapper;


@ExtendWith(MockitoExtension.class)
public class BookingDtoServiceImplTest {

    @Mock
    private UserDtoService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingDtoServiceImpl bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final User owner = User.builder()
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
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.APPROVED)
            .item(item)
            .booker(user)
            .build();

    private final Booking bookingWaiting = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(user)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();


    private final BookingDto bookingDtoEndBeforeStart = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();


    @Test
    void create() {
        BookingDtoOut expectedBookingDtoOut = bookingMapper.toBookingOut(bookingMapper.toBooking(user, item, bookingDto));
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingMapper.toBooking(user, item, bookingDto));

        BookingDtoOut actualBookingDtoOut = bookingService.add(userDto.getId(), bookingDto);

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }


    @Test
    void createWhenEndIsBeforeStartShouldThrowValidationException() {
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotValidDataException bookingValidationException = assertThrows(NotValidDataException.class,
                () -> bookingService.add(userDto.getId(), bookingDtoEndBeforeStart));

        assertEquals(bookingValidationException.getMessage(), "Дата окончания не может быть раньше или равна дате начала");
    }

    @Test
    void createWhenItemIsNotAvailableShouldThrowValidationException() {
        item.setAvailable(false);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotValidDataException bookingValidationException = assertThrows(NotValidDataException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(bookingValidationException.getMessage(), "Вещь не доступна для бронирования.");
    }

    @Test
    void createWhenItemOwnerEqualsBookerShouldThrowValidationException() {
        item.setOwner(user);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        EntityNotFoundException bookingNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(bookingNotFoundException.getMessage(), "Вещь не найдена.");
    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoOut actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(BookingStatus.APPROVED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateWhenStatusNotApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoOut actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), false);

        assertEquals(BookingStatus.REJECTED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateShouldStatusNotWaiting() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotValidDataException bookingValidationException = assertThrows(NotValidDataException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), false));

        assertEquals(bookingValidationException.getMessage(), "Бронь cо статусом не WAITING");
    }

    @Test
    void updateWhenUserIsNotItemOwnerShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        EntityNotFoundException bookingNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.update(userDto.getId(), booking.getId(), true));

        assertEquals(bookingNotFoundException.getMessage(), "Пользователь не является владельцем");
    }

    @Test
    void getById() {
        BookingDtoOut expectedBookingDtoOut = bookingMapper.toBookingOut(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoOut actualBookingDtoOut = bookingService.findBookingByUserId(user.getId(), booking.getId());

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void getByIdWhenBookingIdIsNotValidShouldThrowObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException bookingNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.findBookingByUserId(1L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Бронь не найдена.");
    }

    @Test
    void getByIdWhenUserIsNotItemOwnerShouldThrowObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        EntityNotFoundException bookingNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.findBookingByUserId(3L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Пользователь не владелец вещи и не автор бронирования");
    }

    @Test
    void getAllByBookerWhenBookingStateAll() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAll(user.getId(), "ALL", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateCURRENT() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllCurrentBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAll(user.getId(), "CURRENT", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStatePAST() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAll(user.getId(), "PAST", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateFUTURE() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllFutureBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAll(user.getId(), "FUTURE", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateWAITING() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllWaitingBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAll(user.getId(), "WAITING", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }


    @Test
    void getAllByBookerWhenBookingStateIsNotValidShouldThrowIllegalArgumentException() {
        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.findAll(user.getId(), "ERROR", PageRequest.of(0, 10)));
    }

    @Test
    void getAllByOwnerWhenBookingStateAll() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "ALL", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateCURRENT() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllCurrentBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "CURRENT", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStatePAST() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "PAST", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateFUTURE() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllFutureBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "FUTURE", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateWAITING() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllWaitingBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "WAITING", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateREJECTED() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllRejectedBookingsByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "REJECTED", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);

    }

    @Test
    void getAllByBookerWhenBookingStateREJECTED() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(bookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllRejectedBookingsByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findAll(user.getId(), "REJECTED", PageRequest.of(0, 10));

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateIsNotValidThenThrowIllegalArgumentException() {
        when(userService.findById(user.getId())).thenReturn(userDto);

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.findAllOwner(user.getId(), "ERROR", PageRequest.of(0, 10)));
    }
}
