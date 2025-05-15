package com.carpooling.api.repository;

import com.carpooling.api.entity.Booking;
import com.carpooling.api.entity.Ride;
import com.carpooling.api.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassengerId(Long passengerId);
    List<Booking> findByRideId(Long rideId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByBookingDateAfter(LocalDateTime date);
    List<Booking> findByRideIdAndStatus(Long rideId, BookingStatus status);
    boolean existsByRideIdAndPassengerId(Long rideId, Long passengerId);
    List<Booking> findByRide(Ride ride);
} 