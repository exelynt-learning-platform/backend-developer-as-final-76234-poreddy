package com.example.booking.controller;

import com.example.booking.dto.ReservationDto;
import com.example.booking.entity.AppUser;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.ResourceEntity;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    record CreateReservationRequest(@NotNull Long resourceId, @NotNull OffsetDateTime startTime, @NotNull OffsetDateTime endTime, @NotNull @DecimalMin("0.0") BigDecimal price) {}

    private final com.example.booking.service.ReservationService reservationService;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationController(com.example.booking.service.ReservationService reservationService, ResourceRepository resourceRepository, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<ReservationDto> list(@RequestParam(required = false) ReservationStatus status,
                                     @RequestParam(required = false) BigDecimal minPrice,
                                     @RequestParam(required = false) BigDecimal maxPrice,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String sort,
                                     Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, sort == null ? Sort.unsorted() : Sort.by(sort));
        // If user is admin, show all filtered results; otherwise restrict to their own filtered results
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Page<Reservation> resPage = admin ? reservationService.filterAll(status, minPrice, maxPrice, pageable)
                : reservationService.filterByUser(userRepository.findByUsername(authentication.getName()).orElseThrow(), status, minPrice, maxPrice, pageable);
        return resPage.map(r -> new ReservationDto(r.getId(), r.getResource().getId(), r.getResource().getName(), r.getUser().getUsername(), r.getStartTime(), r.getEndTime(), r.getPrice(), r.getStatus()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ReservationDto> create(@Valid @RequestBody CreateReservationRequest req, Authentication authentication) {
        Optional<ResourceEntity> resource = resourceRepository.findById(req.resourceId());
        if (resource.isEmpty()) return ResponseEntity.badRequest().build();
        AppUser user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        if (!req.startTime().isBefore(req.endTime())) return ResponseEntity.badRequest().build();
        Reservation r = new Reservation();
        r.setResource(resource.get());
        r.setUser(user);
        r.setStartTime(req.startTime());
        r.setEndTime(req.endTime());
        r.setPrice(req.price());
        r.setStatus(ReservationStatus.PENDING);
        r = reservationService.create(r);
        return ResponseEntity.created(URI.create("/api/reservations/" + r.getId())).body(new ReservationDto(r.getId(), r.getResource().getId(), r.getResource().getName(), r.getUser().getUsername(), r.getStartTime(), r.getEndTime(), r.getPrice(), r.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> get(@PathVariable Long id, Authentication authentication) {
        Optional<Reservation> opt = reservationService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Reservation r = opt.get();
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !r.getUser().getUsername().equals(authentication.getName())) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(new ReservationDto(r.getId(), r.getResource().getId(), r.getResource().getName(), r.getUser().getUsername(), r.getStartTime(), r.getEndTime(), r.getPrice(), r.getStatus()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> update(@PathVariable Long id, @RequestBody ReservationDto dto) {
        return reservationService.findById(id).map(r -> {
            if (dto.status() != null) r.setStatus(dto.status());
            reservationService.update(id, r);
            return ResponseEntity.ok(new ReservationDto(r.getId(), r.getResource().getId(), r.getResource().getName(), r.getUser().getUsername(), r.getStartTime(), r.getEndTime(), r.getPrice(), r.getStatus()));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return reservationService.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
