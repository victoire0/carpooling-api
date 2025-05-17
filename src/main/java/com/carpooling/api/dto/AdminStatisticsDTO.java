package com.carpooling.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AdminStatisticsDTO {
    private Long totalUsers;
    private Long totalDrivers;
    private Long totalPassengers;
    private Long totalRides;
    private Long totalBookings;
    private Long pendingVerifications;
    private Map<String, Long> ridesByStatus;
    private Map<String, Long> bookingsByStatus;
    private Map<String, Long> usersByRole;
    private Map<String, Long> ridesByMonth;
    private Map<String, Long> bookingsByMonth;
    private Double averageRating;
    private LocalDateTime lastUpdate;
} 