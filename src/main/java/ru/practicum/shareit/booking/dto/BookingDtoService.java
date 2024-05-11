package ru.practicum.shareit.booking.dto;

import java.util.List;

public interface BookingDtoService {

    BookingDtoOut add(Long userId, BookingDto bookingDto);

    BookingDtoOut update(Long userId, Long bookingId, Boolean approved);

    BookingDtoOut findBookingByUserId(Long userId, Long bookingId);

    List<BookingDtoOut> findAll(Long userId, String state, Integer from, Integer size);

    List<BookingDtoOut> findAllOwner(Long userId, String state, Integer from, Integer size);
}