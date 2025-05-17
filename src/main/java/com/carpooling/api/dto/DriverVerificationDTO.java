package com.carpooling.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DriverVerificationDTO {
    private Long id;
    private Long userId;
    private String username;
    private String idCardUrl;
    private String licenseUrl;
    private String vehiclePhotoUrl;
    private String status;
    private LocalDateTime verificationDate;
    private String adminComment;
} 