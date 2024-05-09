package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :user_id " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByBookerId(@Param("user_id") Long userId);


    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :booker_id " +
            "AND :time BETWEEN b.start_date AND b.end_date " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllCurrentBookingsByBookerId(@Param("booker_id") Long bookerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :booker_id " +
            "AND b.end_date < :time " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllPastBookingsByBookerId(@Param("booker_id") Long bookerId, @Param("time") LocalDateTime currentTime);


    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :booker_id " +
            "AND b.start_date > :time " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllFutureBookingsByBookerId(@Param("booker_id") Long bookerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :booker_id " +
            "AND b.status = 'WAITING' " +
            "AND b.start_date > :time " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllWaitingBookingsByBookerId(@Param("booker_id") Long bookerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :booker_id " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllRejectedBookingsByBookerId(@Param("booker_id") Long bookerId);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id  " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllBookingsByOwnerId(@Param("item_id") Long ownerId);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = :owner_id " +
            "AND :time BETWEEN b.start_date AND b.end_date " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllCurrentBookingsByOwnerId(@Param("owner_id") Long ownerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = :owner_id " +
            "AND b.end_date < :time " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllPastBookingsByOwnerId(@Param("owner_id") Long ownerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = :owner_id " +
            "AND b.start_date > :time " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllFutureBookingsByOwnerId(@Param("owner_id") Long ownerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = :owner_id " +
            "AND b.status = 'WAITING' " +
            "AND b.start_date > :time " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllWaitingBookingsByOwnerId(@Param("owner_id") Long ownerId, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = :owner_id " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllRejectedBookingsByOwnerId(@Param("owner_id") Long ownerId);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = :item_id " +
            "AND b.start_date < :time " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getLastBooking(@Param("item_id") Long idItem, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = :item_id " +
            "AND b.start_date > :time " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date ASC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNextBooking(@Param("item_id") Long idItem, @Param("time") LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = :user_id " +
            "AND i.id = :item_id " +
            "AND b.status = 'APPROVED' " +
            "AND b.end_date < :time ", nativeQuery = true)
    List<Booking> findAllByUserBookings(@Param("user_id") Long userId, @Param("item_id") Long itemId, @Param("time") LocalDateTime now);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, BookingStatus status);

    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, BookingStatus bookingStatus);
}