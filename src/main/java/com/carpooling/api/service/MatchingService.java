package com.carpooling.api.service;

import com.carpooling.api.dto.RideDTO;
import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.User;
import com.carpooling.api.entity.Booking;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.UserRepository;
import com.carpooling.api.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public List<RideDTO> findMatchingRides(Long userId, String departureLocation, 
                                         String arrivalLocation, LocalDateTime departureTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Ride> matchingRides = rideRepository.findByDepartureLocationAndArrivalLocationAndDepartureTimeBetween(
                departureLocation,
                arrivalLocation,
                departureTime.minusHours(1),
                departureTime.plusHours(1)
        );

        return matchingRides.stream()
                .filter(ride -> {
                    if (ride.getDriver().getRating() < 3.5) return false;
                    
                    if (ride.getAvailableSeats() <= 0) return false;
                    
                    if (ride.getBookings().stream()
                            .anyMatch(booking -> booking.getPassenger().getId().equals(userId))) {
                        return false;
                    }
                    
                    return true;
                })
                .map(this::convertToRideDTO)
                .collect(Collectors.toList());
    }

    public List<Booking> findMatchingBookings(Ride ride) {
        return bookingRepository.findByRide(ride);
    }

    private RideDTO convertToRideDTO(Ride ride) {
        return new RideDTO();
    }
} 