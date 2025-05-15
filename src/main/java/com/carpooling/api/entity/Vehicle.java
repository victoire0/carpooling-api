package com.carpooling.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String picture;

    @Column(name = "insurance_number")
    private String insuranceNumber;

    @Column(name = "insurance_expiry_date")
    private LocalDateTime insuranceExpiryDate;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "registration_expiry_date")
    private LocalDateTime registrationExpiryDate;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 