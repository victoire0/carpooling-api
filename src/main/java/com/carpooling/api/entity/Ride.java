package com.carpooling.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.carpooling.api.enums.RideStatus;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "departure_location", nullable = false)
    private String departureLocation;

    @Column(name = "departure_coordinates", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point departureCoordinates;

    @Column(name = "arrival_location", nullable = false)
    private String arrivalLocation;

    @Column(name = "arrival_coordinates", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point arrivalCoordinates;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "price_per_seat", nullable = false)
    private BigDecimal pricePerSeat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RideStatus status = RideStatus.SCHEDULED;

    @Column(columnDefinition = "geometry(LineString,4326)")
    private LineString route;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private String vehicleType;

    @Column
    private String vehicleModel;

    @Column
    private String vehicleColor;

    @Column
    private String vehiclePlate;

    @Column
    private Double distance;

    @Column
    private Integer duration;

    @Column
    @Builder.Default
    private Boolean isNegotiable = false;

    @Column
    private String description;

    @Column
    private String notes;

    @Column
    private Double currentLatitude;

    @Column
    private Double currentLongitude;

    @Column
    private LocalDateTime lastLocationUpdateTime;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == RideStatus.ACTIVE || status == RideStatus.IN_PROGRESS;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void setCurrentLatitude(Double latitude) {
        this.currentLatitude = latitude;
    }

    public void setCurrentLongitude(Double longitude) {
        this.currentLongitude = longitude;
    }

    public void setLastLocationUpdateTime(LocalDateTime time) {
        this.lastLocationUpdateTime = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 