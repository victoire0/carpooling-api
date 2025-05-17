package com.carpooling.api.service;

import com.carpooling.api.dto.AuthResponse;
import com.carpooling.api.dto.LoginRequest;
import com.carpooling.api.dto.RegisterRequest;
import com.carpooling.api.dto.UserDTO;
import com.carpooling.api.entity.Driver;
import com.carpooling.api.entity.Passenger;
import com.carpooling.api.entity.User;
import com.carpooling.api.repository.UserRepository;
import com.carpooling.api.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WelcomeEmailService welcomeEmailService;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.error("L'email est déjà utilisé");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthResponse.error("Le nom d'utilisateur est déjà utilisé");
        }

        try {
            User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .isActive(true)
                .isDriver(request.getIsDriver())
                .isVerified(false)
                .welcomeEmailSent(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            Passenger passenger = new Passenger();
            passenger.setUser(user);
            passenger.setIsVerified(false);
            passenger.setRating(0.0);
            passenger.setCompletedRides(0);
            passenger.setCancelledRides(0);
            user.setPassenger(passenger);

            if (Boolean.TRUE.equals(request.getIsDriver())) {
                Driver driver = new Driver();
                driver.setUser(user);
                driver.setIsVerified(false);
                driver.setRating(0.0);
                driver.setCompletedRides(0);
                driver.setTotalRides(0);
                user.setDriver(driver);
            }

            user = userRepository.save(user);

            String jwtToken = jwtService.generateToken(user);
            
            UserDTO userDTO = UserDTO.fromEntity(user);
            
            return AuthResponse.success(jwtToken, userDTO);
        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement: {}", e.getMessage(), e);
            return AuthResponse.error("Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return AuthResponse.error("Mot de passe incorrect");
            }

            if (!user.isWelcomeEmailSent()) {
                try {
                    welcomeEmailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
                    user.setWelcomeEmailSent(true);
                    userRepository.save(user);
                    log.info("Email de bienvenue envoyé à l'utilisateur {} lors de sa première connexion", user.getEmail());
                } catch (Exception e) {
                    log.error("Erreur lors de l'envoi de l'email de bienvenue: {}", e.getMessage(), e);
                }
            }

            String jwtToken = jwtService.generateToken(user);
            
            UserDTO userDTO = UserDTO.fromEntity(user);
            
            return AuthResponse.success(jwtToken, userDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la connexion: {}", e.getMessage(), e);
            return AuthResponse.error("Erreur lors de la connexion: " + e.getMessage());
        }
    }
}