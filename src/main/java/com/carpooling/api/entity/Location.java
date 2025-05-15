package com.carpooling.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point coordinates;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column
    private String postalCode;

    @Column
    private String placeId; // Identifiant unique de l'emplacement

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
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

    public Double getLatitude() {
        return coordinates != null ? coordinates.getY() : latitude;
    }

    public Double getLongitude() {
        return coordinates != null ? coordinates.getX() : longitude;
    }
} 