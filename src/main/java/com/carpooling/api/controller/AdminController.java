package com.carpooling.api.controller;

import com.carpooling.api.dto.RideDTO;
import com.carpooling.api.dto.UserDTO;
import com.carpooling.api.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/rides")
    public ResponseEntity<List<RideDTO>> getAllRides() {
        return ResponseEntity.ok(adminService.getAllRides());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{userId}/block")
    public ResponseEntity<UserDTO> blockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.blockUser(userId));
    }
} 