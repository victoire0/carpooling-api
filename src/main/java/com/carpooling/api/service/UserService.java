package com.carpooling.api.service;

import com.carpooling.api.dto.UserDTO;
import com.carpooling.api.entity.Driver;
import com.carpooling.api.entity.Passenger;
import com.carpooling.api.entity.User;
import com.carpooling.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        if (user.getPassenger() == null) {
            Passenger passenger = new Passenger();
            passenger.setUser(user);
            passenger.setRating(0.0);
            passenger.setCompletedRides(0);
            passenger.setCancelledRides(0);
            user.setPassenger(passenger);
        }
        
        if (Boolean.TRUE.equals(user.getIsDriver()) && user.getDriver() == null) {
            Driver driver = new Driver();
            driver.setUser(user);
            driver.setRating(0.0);
            driver.setCompletedRides(0);
            driver.setTotalRides(0);
            user.setDriver(driver);
        }
        
        return convertToDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        
        if (userDTO.getRating() != null) {
            user.setRating(userDTO.getRating());
        }
        
        if (userDTO.getCompletedRides() != null) {
            user.setCompletedRides(userDTO.getCompletedRides());
        }
        
        if (userDTO.getTotalRides() != null) {
            user.setTotalRides(userDTO.getTotalRides());
        }
        
        return convertToDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .map(user -> {
                    user.setIsActive(false);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getLoginUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .rating(user.getRating())
                .completedRides(user.getCompletedRides())
                .totalRides(user.getTotalRides())
                .isActive(user.getIsActive())
                .isDriver(user.getIsDriver())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
                
        return dto;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .isDriver(userDTO.getIsDriver())
                .isActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : true)
                .isVerified(userDTO.getIsVerified() != null ? userDTO.getIsVerified() : false)
                .build();
                
        return user;
    }
}