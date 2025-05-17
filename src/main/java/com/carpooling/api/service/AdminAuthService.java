package com.carpooling.api.service;

import com.carpooling.api.dto.AdminAuthRequest;
import com.carpooling.api.dto.AdminRegisterRequest;
import com.carpooling.api.entity.Admin;
import com.carpooling.api.entity.Role;
import com.carpooling.api.repository.AdminRepository;
import com.carpooling.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> registerAdmin(AdminRegisterRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        
        admin.setRole(request.getRole() != null ? request.getRole() : Role.ADMIN);

        admin = adminRepository.save(admin);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");
        response.put("adminId", admin.getId());
        response.put("email", admin.getEmail());
        response.put("firstName", admin.getFirstName());
        response.put("lastName", admin.getLastName());
        response.put("role", admin.getRole().name());

        return response;
    }

    public Map<String, Object> authenticateAdmin(AdminAuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        Admin admin = adminRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Admin non trouvé"));
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");
        response.put("adminId", admin.getId());
        response.put("email", admin.getEmail());
        response.put("firstName", admin.getFirstName());
        response.put("lastName", admin.getLastName());
        response.put("role", admin.getRole().name());

        return response;
    }
}