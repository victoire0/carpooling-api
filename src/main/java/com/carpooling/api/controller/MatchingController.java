package com.carpooling.api.controller;

import com.carpooling.api.dto.RideDTO;
import com.carpooling.api.service.MatchingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/rides")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RideDTO>> findMatchingRides(
            Authentication authentication,
            @RequestParam String departureLocation,
            @RequestParam String arrivalLocation,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {
        
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(matchingService.findMatchingRides(
                userId, departureLocation, arrivalLocation, departureTime));
    }
} 