package com.carpooling.api.repository;

import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.User;
import com.carpooling.api.enums.RideStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriver(User driver);
    
    // Ajout de la méthode manquante
    List<Ride> findByDriverId(Long driverId);
    
    List<Ride> findByStatus(RideStatus status);
    List<Ride> findByDriverAndStatus(User driver, RideStatus status);
    
    @Query("SELECT r FROM Ride r WHERE r.departureTime > :now AND r.status = :status AND r.availableSeats > 0")
    List<Ride> findAvailableRides(@Param("now") LocalDateTime now, @Param("status") RideStatus status);
    
    @Query(value = "SELECT * FROM rides WHERE ST_DWithin(departure_coordinates, :point, :distance) AND status = :status AND available_seats > 0", nativeQuery = true)
    List<Ride> findNearbyRides(@Param("point") Point point, @Param("distance") double distance, @Param("status") String status);
    
    @Query(value = "SELECT * FROM rides WHERE ST_DWithin(arrival_coordinates, :point, :distance) AND status = :status AND available_seats > 0", nativeQuery = true)
    List<Ride> findRidesNearDestination(@Param("point") Point point, @Param("distance") double distance, @Param("status") String status);

    List<Ride> findByDepartureLocationAndArrivalLocationAndDepartureTimeBetween(
        String departureLocation,
        String arrivalLocation,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
    
    // Ajout de la méthode manquante
    List<Ride> findByDepartureLocationAndArrivalLocationAndDepartureTimeAfter(
        String departureLocation,
        String arrivalLocation,
        LocalDateTime departureTime
    );
}