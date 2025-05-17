package com.carpooling.api.service;

import com.carpooling.api.dto.RideDTO;
import com.carpooling.api.dto.UserDTO;
import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.User;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public List<RideDTO> getAllRides() {
        return rideRepository.findAll()
                .stream()
                .map(this::convertToRideDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        return convertToUserDTO(userRepository.save(user));
    }

    private RideDTO convertToRideDTO(Ride ride) {
        RideDTO dto = new RideDTO();
        dto.setId(ride.getId());
        dto.setDriverId(ride.getDriver().getId());
        dto.setDepartureLocation(ride.getDepartureLocation());
        dto.setArrivalLocation(ride.getArrivalLocation());
        dto.setDepartureTime(ride.getDepartureTime());
        dto.setArrivalTime(ride.getArrivalTime());
        dto.setTotalSeats(ride.getTotalSeats());
        dto.setAvailableSeats(ride.getAvailableSeats());
        dto.setPricePerSeat(ride.getPricePerSeat());
        dto.setStatus(ride.getStatus());
        return dto;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIsActive(user.getIsActive());
        return dto;
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .rating(user.getRating())
                .completedRides(user.getCompletedRides())
                .totalRides(user.getTotalRides())
                .build();
    }
} 