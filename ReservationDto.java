package com.example.booking.dto;

import com.example.booking.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ReservationDto(Long id, Long resourceId, String resourceName, String username,
                             OffsetDateTime startTime, OffsetDateTime endTime,
                             BigDecimal price, ReservationStatus status) {}
