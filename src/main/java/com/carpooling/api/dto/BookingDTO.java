package com.carpooling.api.dto;

import com.carpooling.api.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDTO {
    private Long id;
    private Long rideId;
    private Long passengerId;
    private String passengerUsername;
    private int seats;
    private BigDecimal price;
    private BookingStatus status;
    private LocalDateTime bookingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String pickupLocation;
    private String dropoffLocation;
    private String negotiationNote;
} 