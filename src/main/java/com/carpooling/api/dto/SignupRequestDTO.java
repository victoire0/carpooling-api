package com.carpooling.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SignupRequestDTO {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String status;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 