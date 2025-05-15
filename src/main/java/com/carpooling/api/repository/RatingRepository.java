package com.carpooling.api.repository;

import com.carpooling.api.entity.Rating;
import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRide(Ride ride);
    List<Rating> findByRated(User rated);
    List<Rating> findByRater(User rater);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.rated = :user")
    Double getAverageRatingForUser(User user);
}