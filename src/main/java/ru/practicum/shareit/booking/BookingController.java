package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingDtoService bookingService;

    @PostMapping
    public BookingDtoOut create(@RequestHeader(USER_HEADER) Long userId,
                                @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateStatus(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable("bookingId")
                                      Long bookingId,
                                      @RequestParam(name = "approved") Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(USER_HEADER) Long userId,
                                         @PathVariable("bookingId")
                                         Long bookingId) {
        return bookingService.findBookingByUserId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findAll(@RequestHeader(USER_HEADER) Long userId,
                                       @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
                                       @Valid @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                       @Valid @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return bookingService.findAll(userId, bookingState, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                           @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
                                           @Valid @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @Valid @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return bookingService.findAllOwner(ownerId, bookingState, PageRequest.of(from / size, size));
    }
}