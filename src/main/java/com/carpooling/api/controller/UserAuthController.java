package com.carpooling.api.controller;

import com.carpooling.api.dto.AuthResponse;
import com.carpooling.api.dto.LoginRequest;
import com.carpooling.api.dto.RegisterRequest;
import com.carpooling.api.service.AuthService;
import com.carpooling.api.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserAuthController {

    private final AuthService authService;
    private final OAuth2Service oauth2Service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/oauth-success")
    public ResponseEntity<AuthResponse> oauthSuccess(@AuthenticationPrincipal OAuth2User principal) {
    if (principal == null) {
        return ResponseEntity.status(401).body(AuthResponse.error("OAuth2 authentication failed"));
    }
    return ResponseEntity.ok(oauth2Service.processOAuth2User(principal));
    }
}