package com.example.booking.service;

import com.example.booking.dto.ReservationDto;
import com.example.booking.entity.AppUser;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.ResourceEntity;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;

    public ReservationService(ReservationRepository reservationRepository, ResourceRepository resourceRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
    }

    public Page<Reservation> filterAll(ReservationStatus status, BigDecimal min, BigDecimal max, Pageable pageable) {
        return reservationRepository.filter(status, min, max, pageable);
    }

    public Page<Reservation> filterByUser(AppUser user, ReservationStatus status, BigDecimal min, BigDecimal max, Pageable pageable) {
        return reservationRepository.filterByUser(user, status, min, max, pageable);
    }

    public Reservation create(Reservation r) {
        // basic validation already handled in controller
        return reservationRepository.save(r);
    }

    public Optional<Reservation> findById(Long id) { return reservationRepository.findById(id); }

    public Optional<Reservation> update(Long id, Reservation updated) {
        return reservationRepository.findById(id).map(r -> {
            if (updated.getStatus() != null) r.setStatus(updated.getStatus());
            reservationRepository.save(r);
            return r;
        });
    }

    public boolean delete(Long id) {
        if (!reservationRepository.existsById(id)) return false;
        reservationRepository.deleteById(id);
        return true;
    }
}
