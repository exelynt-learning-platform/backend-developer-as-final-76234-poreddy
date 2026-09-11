package com.example.booking.repository;

import com.example.booking.entity.AppUser;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByUser(AppUser user, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE (:status IS NULL OR r.status = :status) " +
            "AND (:min IS NULL OR r.price >= :min) AND (:max IS NULL OR r.price <= :max)")
    Page<Reservation> filter(@Param("status") ReservationStatus status,
                             @Param("min") BigDecimal min,
                             @Param("max") BigDecimal max,
                             Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND (:status IS NULL OR r.status = :status) " +
            "AND (:min IS NULL OR r.price >= :min) AND (:max IS NULL OR r.price <= :max)")
    Page<Reservation> filterByUser(@Param("user") AppUser user,
                                   @Param("status") ReservationStatus status,
                                   @Param("min") BigDecimal min,
                                   @Param("max") BigDecimal max,
                                   Pageable pageable);
}
