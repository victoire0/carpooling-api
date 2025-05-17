package com.carpooling.api.controller;

import com.carpooling.api.dto.AdminAuthRequest;
import com.carpooling.api.dto.AdminRegisterRequest;
import com.carpooling.api.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('SUPER_ADMIN')")  // Seul un super-admin peut créer un nouvel admin
    public ResponseEntity<Map<String, Object>> register(@RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(adminAuthService.registerAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AdminAuthRequest request) {
        return ResponseEntity.ok(adminAuthService.authenticateAdmin(request));
    }
} 