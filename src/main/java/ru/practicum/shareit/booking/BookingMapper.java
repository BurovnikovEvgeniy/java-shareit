package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface BookingMapper {

    BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "status", constant = "WAITING")
    Booking toBooking(User user, Item item, BookingDto bookingDto);

    BookingDtoOut toBookingOut(Booking booking);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    BookingItemDto toBookingItemDto(Booking booking);
}
