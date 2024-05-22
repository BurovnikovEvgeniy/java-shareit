package ru.practicum.shareit.booking.dto;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingDtoService {

    BookingDtoOut add(Long userId, BookingDto bookingDto);

    BookingDtoOut update(Long userId, Long bookingId, Boolean approved);

    BookingDtoOut findBookingByUserId(Long userId, Long bookingId);

    List<BookingDtoOut> findAll(Long userId, String state, Pageable pageable);

    List<BookingDtoOut> findAllOwner(Long userId, String state, Pageable pageable);
}