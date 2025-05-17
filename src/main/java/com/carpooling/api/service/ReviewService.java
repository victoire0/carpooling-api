package com.carpooling.api.service;

import com.carpooling.api.dto.ReviewDTO;
import com.carpooling.api.entity.*;
import com.carpooling.api.enums.BookingStatus;
import com.carpooling.api.repository.ReviewRepository;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, RideRepository rideRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    public ReviewDTO createReview(Long rideId, ReviewDTO reviewDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User reviewer = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        
        User driverAsUser = getUserFromDriver(ride.getDriver());
        
        User reviewed = driverAsUser.equals(reviewer) ? 
                ride.getBookings().stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.CONFIRMED))
                        .map(booking -> booking.getPassenger().getUser())
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No confirmed passenger found")) :
                driverAsUser;

        Review review = new Review();
        review.setRide(ride);
        review.setReviewer(reviewer);
        review.setReviewed(reviewed);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    private User getUserFromDriver(Object driver) {
        if (driver instanceof User) {
            return (User) driver;
        }
        
        if (driver instanceof Driver) {
            Driver driverObj = (Driver) driver;
            try {
                java.lang.reflect.Method getUserMethod = driverObj.getClass().getMethod("getUser");
                return (User) getUserMethod.invoke(driverObj);
            } catch (Exception e1) {
                try {
                    java.lang.reflect.Field userField = driverObj.getClass().getDeclaredField("user");
                    userField.setAccessible(true);
                    return (User) userField.get(driverObj);
                } catch (Exception e2) {
                    try {
                        java.lang.reflect.Method getIdMethod = driverObj.getClass().getMethod("getId");
                        Long driverId = (Long) getIdMethod.invoke(driverObj);
                        return userRepository.findById(driverId)
                            .orElseThrow(() -> new RuntimeException("User not found for driver ID: " + driverId));
                    } catch (Exception e3) {
                        throw new RuntimeException("Cannot convert Driver to User", e3);
                    }
                }
            }
        }
        
        throw new RuntimeException("Cannot convert object of type " + 
                                  (driver != null ? driver.getClass().getName() : "null") + 
                                  " to User");
    }

    public List<ReviewDTO> getUserReviews(Long userId) {
        return reviewRepository.findByReviewedId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getRideReviews(Long rideId) {
        return reviewRepository.findByRideId(rideId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public List<Review> getReviewsForRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new EntityNotFoundException("Ride not found"));
        return reviewRepository.findByRide(ride);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRideId(review.getRide().getId());
        dto.setReviewerId(review.getReviewer().getId());
        dto.setReviewedId(review.getReviewed().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}