package com.carpooling.api.controller;

import com.carpooling.api.dto.ReviewDTO;
import com.carpooling.api.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/ride/{rideId}")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('DRIVER')")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long rideId,
            @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(rideId, reviewDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<ReviewDTO>> getRideReviews(@PathVariable Long rideId) {
        return ResponseEntity.ok(reviewService.getRideReviews(rideId));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isReviewAuthor(#reviewId)")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
} 