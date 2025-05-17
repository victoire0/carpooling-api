package com.carpooling.api.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class FinancialDashboardDTO {
    private Double totalEarnings;
    private Double monthlyEarnings;
    private Double weeklyEarnings;
    private Double averageEarningPerRide;
    private Integer totalRides;
    private Integer completedRides;
    private Integer pendingRides;
    private List<PaymentDTO> recentPayments;
    private List<MonthlyEarningDTO> monthlyEarningsHistory;
    private LocalDate startDate;
    private LocalDate endDate;
} 