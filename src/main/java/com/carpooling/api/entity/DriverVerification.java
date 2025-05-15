package com.carpooling.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.carpooling.api.enums.VerificationStatus;

@Data
@Entity
@Table(name = "driver_verifications")
public class DriverVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "license_expiry")
    private LocalDateTime licenseExpiry;

    @Column(name = "license_url")
    private String licenseUrl;

    @Column(name = "id_card_url")
    private String idCardUrl;

    @Column(name = "vehicle_photo_url")
    private String vehiclePhotoUrl;

    @Column(name = "license_image")
    private String licenseImage;

    @Column(name = "id_card_image")
    private String idCardImage;

    @Column(name = "vehicle_registration")
    private String vehicleRegistration;

    @Column(name = "vehicle_insurance")
    private String vehicleInsurance;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        verificationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 