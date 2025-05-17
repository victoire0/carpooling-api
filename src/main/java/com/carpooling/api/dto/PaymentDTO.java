package com.carpooling.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentDate;
    private String paymentMethod;
} 