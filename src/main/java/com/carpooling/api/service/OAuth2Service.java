package com.carpooling.api.service;

import com.carpooling.api.dto.AuthResponse;
import com.carpooling.api.dto.UserDTO;
import com.carpooling.api.entity.Driver;
import com.carpooling.api.entity.Passenger;
import com.carpooling.api.entity.User;
import com.carpooling.api.repository.UserRepository;
import com.carpooling.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final WelcomeEmailService welcomeEmailService;

    @Transactional
    public AuthResponse processOAuth2User(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return AuthResponse.error("OAuth2 authentication failed");
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        if (email == null) {
            return AuthResponse.error("Email not found in OAuth2 attributes");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("Utilisateur existant trouvé avec l'email: {}", email);
        } else {
            user = createNewUserFromOAuth2(attributes);
            log.info("Nouvel utilisateur OAuth2 créé avec l'email: {}", email);
        }

        if (!user.isWelcomeEmailSent()) {
            try {
                welcomeEmailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
                user.setWelcomeEmailSent(true);
                userRepository.save(user);
                log.info("Email de bienvenue envoyé à l'utilisateur OAuth2 {} lors de sa première connexion", user.getEmail());
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email de bienvenue: {}", e.getMessage(), e);
            }
        }

        String jwtToken = jwtService.generateToken(user);
        UserDTO userDTO = UserDTO.fromEntity(user);
        return AuthResponse.success(jwtToken, userDTO);
    }

    private User createNewUserFromOAuth2(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.getOrDefault("given_name", "");
        String lastName = (String) attributes.getOrDefault("family_name", "");
        String name = (String) attributes.getOrDefault("name", "");
        
        if (firstName.isEmpty() && lastName.isEmpty() && !name.isEmpty()) {
           
            String[] parts = name.split(" ");
            if (parts.length > 0) {
                firstName = parts[0];
                if (parts.length > 1) {
                    lastName = parts[parts.length - 1];
                }
            }
        }

        String username = email.split("@")[0];
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = email.split("@")[0] + counter;
            counter++;
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password("") // Pas de mot de passe pour les utilisateurs OAuth2
                .isActive(true)
                .isDriver(false) // Par défaut, pas un conducteur
                .isVerified(true) // Les utilisateurs OAuth2 sont considérés comme vérifiés
                .welcomeEmailSent(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Passenger passenger = new Passenger();
        passenger.setUser(user);
        passenger.setIsVerified(true);
        passenger.setRating(0.0);
        passenger.setCompletedRides(0);
        passenger.setCancelledRides(0);
        user.setPassenger(passenger);

        return userRepository.save(user);
    }
}