package com.carpooling.api.service;

import com.carpooling.api.entity.Location;
import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.User;
import com.carpooling.api.entity.Booking;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.BookingRepository;
import com.carpooling.api.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RealTimeTrackingService {

    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, Location> lastKnownLocations = new HashMap<>();

    public void updatePassengerLocation(Long rideId, Long passengerId, Location location) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        if (!ride.isActive()) {
            throw new RuntimeException("Le trajet n'est pas actif");
        }

        lastKnownLocations.put(passengerId, location);

        Map<String, Object> positionUpdate = new HashMap<>();
        positionUpdate.put("passengerId", passengerId);
        positionUpdate.put("latitude", location.getLatitude());
        positionUpdate.put("longitude", location.getLongitude());
        positionUpdate.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend(
            "/topic/ride/" + rideId + "/positions",
            positionUpdate
        );
    }

    public Location getLastKnownLocation(Long passengerId) {
        return lastKnownLocations.get(passengerId);
    }

    public void stopTracking(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        ride.getBookings().stream()
            .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
            .map(booking -> booking.getPassenger())
            .forEach(passenger -> lastKnownLocations.remove(passenger.getId()));
    }

    public void updateRideLocation(Long rideId, Double latitude, Double longitude) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));
        
        ride.setCurrentLatitude(latitude);
        ride.setCurrentLongitude(longitude);
        ride.setLastLocationUpdateTime(LocalDateTime.now());
        
        rideRepository.save(ride);
        
        messagingTemplate.convertAndSend(
            "/topic/ride/" + rideId + "/location",
            Map.of(
                "latitude", latitude,
                "longitude", longitude,
                "timestamp", LocalDateTime.now()
            )
        );
    }

    public List<Booking> getActiveBookingsForRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));
        return bookingRepository.findByRide(ride);
    }
} 