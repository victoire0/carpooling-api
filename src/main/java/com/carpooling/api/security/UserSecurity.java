package com.carpooling.api.security;

import com.carpooling.api.entity.*;
import com.carpooling.api.repository.BookingRepository;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.UserRepository;
import com.carpooling.api.repository.ReviewRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public UserSecurity(UserRepository userRepository, RideRepository rideRepository, BookingRepository bookingRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUsername().equals(currentUsername);
    }

    public boolean isRideDriver(Long rideId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        // Ici - driver est directement un User
        return ride.getDriver().getUser().getUsername().equals(currentUsername);
    }

    public boolean isBookingPassenger(Long bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return booking.getPassenger().getUser().getUsername().equals(currentUsername);
    }

    public boolean isBookingRideDriver(Long bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        // Ici - driver est directement un User
        return booking.getRide().getDriver().getUser().getUsername().equals(currentUsername);
    }

    public boolean isReviewAuthor(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return review.getReviewer().getUsername().equals(currentUsername);
    }
}