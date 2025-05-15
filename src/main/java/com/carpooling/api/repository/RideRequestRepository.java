package com.carpooling.api.repository;

import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.RideRequest;
import com.carpooling.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByRide(Ride ride);
    List<RideRequest> findByPassenger(User passenger);
    List<RideRequest> findByRideAndStatus(Ride ride, RideRequest.RequestStatus status);
    List<RideRequest> findByPassengerAndStatus(User passenger, RideRequest.RequestStatus status);
} 