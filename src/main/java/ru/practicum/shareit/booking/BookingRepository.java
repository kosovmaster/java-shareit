package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findBookingByIdAndUser(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    Collection<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    Collection<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime current);

    Collection<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime current);

    Collection<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime current, LocalDateTime currentDuplicate);

    Collection<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    Collection<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime current);

    Collection<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime current);

    Collection<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime current, LocalDateTime currentDuplicate);


    @Query("SELECT b FROM Booking b WHERE b.id IN " +
            "(SELECT b2.id FROM Booking b2 WHERE b2.item.id IN :itemIds AND b2.start >= :current " +
            "AND b2.start = (SELECT MIN(b3.start) " +
            "FROM Booking b3 WHERE b3.item.id = b2.item.id AND b3.start >= :current AND b.status = :status))")
    List<Booking> findNextBookingsForOwner(@Param("current") LocalDateTime current,
                                           @Param("itemIds") List<Long> itemIds,
                                           @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.id IN " +
            "(SELECT b2.id FROM Booking b2 WHERE b2.item.id IN :itemIds AND b2.start <= :current " +
            "AND b2.start = (SELECT MAX(b3.start) " +
            "FROM Booking b3 WHERE b3.item.id = b2.item.id AND b3.start <= :current AND b.status = :status))")
    List<Booking> findLastBookingsForOwner(@Param("current") LocalDateTime current,
                                           @Param("itemIds") List<Long> itemIds,
                                           @Param("status") BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime current);
}
