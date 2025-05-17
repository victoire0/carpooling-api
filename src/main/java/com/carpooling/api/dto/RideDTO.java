package com.carpooling.api.dto;

import com.carpooling.api.enums.RideStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RideDTO {
    private Long id;
    private Long driverId;
    private String driverUsername;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal pricePerSeat;
    private RideStatus status;
    private String description;
    private String vehicleType;
    private String vehicleModel;
    private String vehicleColor;
    private String vehiclePlate;
    private Double distance;
    private Integer duration;
    private String routePolyline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isNegotiable;
    private String priceNegotiationNote;
    private List<BookingDTO> bookings;
} 