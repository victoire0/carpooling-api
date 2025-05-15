package com.carpooling.api.repository;

import com.carpooling.api.entity.Review;
import com.carpooling.api.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedId(Long userId);
    List<Review> findByRideId(Long rideId);
    List<Review> findByRide(Ride ride);
} 