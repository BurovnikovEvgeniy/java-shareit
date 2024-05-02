package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private long booker;
    private BookingStatus status;
}
