package com.carpooling.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MonthlyEarningDTO {
    private Long id;
    private Long driverId;
    private Integer year;
    private Integer month;
    private Double totalEarnings;
    private Integer totalRides;
    private Integer totalPassengers;
    private Double averageRating;
    private BigDecimal platformFee;
    private BigDecimal netEarnings;
    private Integer totalBookings;
} 