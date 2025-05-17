package com.carpooling.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminStatsDTO {
    private LocalDate statsDate;
    private Long totalUsers;
    private Long totalDrivers;
    private Long totalRides;
    private Long totalBookings;
    private Long pendingVerifications;
    private Long activeRides;
    private Long completedRides;
    private Long pendingBookings;
    private Double averageUserRating;
    private Double averageDriverRating;
} 