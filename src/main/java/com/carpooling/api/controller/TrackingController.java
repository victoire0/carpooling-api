package com.carpooling.api.controller;

import com.carpooling.api.entity.Location;
import com.carpooling.api.service.RealTimeTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final RealTimeTrackingService trackingService;

    @PostMapping("/ride/{rideId}/position")
    public ResponseEntity<?> updatePosition(
            @PathVariable Long rideId,
            @RequestBody Location location,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        trackingService.updatePassengerLocation(rideId, userId, location);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/ride/{rideId}/position")
    @SendTo("/topic/ride/{rideId}/positions")
    public void handlePositionUpdate(
            @PathVariable Long rideId,
            @RequestBody Location location,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        trackingService.updatePassengerLocation(rideId, userId, location);
    }
} 