package com.carpooling.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "license_expiry")
    private LocalDateTime licenseExpiry;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "driver_since")
    private LocalDateTime driverSince;

    @Column(name = "rating")
    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "completed_rides")
    @Builder.Default
    private Integer completedRides = 0;

    @Column(name = "total_rides")
    @Builder.Default
    private Integer totalRides = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "driver")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Ride> rides;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        driverSince = LocalDateTime.now();
        isVerified = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Surcharger la méthode toString pour éviter les références circulaires
    @Override
    public String toString() {
        return "Driver{" +
            "id=" + id +
            ", licenseNumber='" + licenseNumber + '\'' +
            ", rating=" + rating +
            ", completedRides=" + completedRides +
            ", totalRides=" + totalRides +
            '}';
    }
}