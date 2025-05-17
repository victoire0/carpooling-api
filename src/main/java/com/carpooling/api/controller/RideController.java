package com.carpooling.api.controller;

import com.carpooling.api.dto.RideDTO;
import com.carpooling.api.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO> getRideById(@PathVariable Long id) {
        return rideService.getRideById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RideDTO>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @PostMapping
    public ResponseEntity<RideDTO> createRide(@RequestBody RideDTO rideDTO) {
        return ResponseEntity.ok(rideService.createRide(rideDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideDTO> updateRide(@PathVariable Long id, @RequestBody RideDTO rideDTO) {
        return ResponseEntity.ok(rideService.updateRide(id, rideDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.ok().build();
    }
} 