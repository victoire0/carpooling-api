package com.carpooling.api.service;

import com.carpooling.api.dto.DriverVerificationDTO;
import com.carpooling.api.entity.DriverVerification;
import com.carpooling.api.entity.User;
import com.carpooling.api.repository.DriverVerificationRepository;
import com.carpooling.api.repository.UserRepository;
import com.carpooling.api.enums.VerificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DriverVerificationService {
    private final DriverVerificationRepository verificationRepository;
    private final UserRepository userRepository;

    public DriverVerificationService(DriverVerificationRepository verificationRepository, 
                                   UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
    }

    public DriverVerificationDTO submitVerification(Long userId, DriverVerificationDTO verificationDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverVerification verification = new DriverVerification();
        verification.setUser(user);
        verification.setIdCardUrl(verificationDTO.getIdCardUrl());
        verification.setLicenseUrl(verificationDTO.getLicenseUrl());
        verification.setVehiclePhotoUrl(verificationDTO.getVehiclePhotoUrl());
        verification.setStatus(VerificationStatus.PENDING);
        verification.setVerificationDate(LocalDateTime.now());

        return convertToDTO(verificationRepository.save(verification));
    }

    public DriverVerificationDTO updateVerificationStatus(Long verificationId, 
                                                        VerificationStatus status, 
                                                        String adminComment) {
        return verificationRepository.findById(verificationId)
                .map(verification -> {
                    verification.setStatus(status);
                    verification.setAdminComment(adminComment);
                    verification.setVerificationDate(LocalDateTime.now());
                    return convertToDTO(verificationRepository.save(verification));
                })
                .orElseThrow(() -> new RuntimeException("Verification not found"));
    }

    public List<DriverVerificationDTO> getPendingVerifications() {
        return verificationRepository.findByStatus(VerificationStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DriverVerificationDTO getVerificationByUserId(Long userId) {
        return verificationRepository.findByUserId(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Verification not found for user"));
    }

    private DriverVerificationDTO convertToDTO(DriverVerification verification) {
        DriverVerificationDTO dto = new DriverVerificationDTO();
        dto.setId(verification.getId());
        dto.setUserId(verification.getUser().getId());
        dto.setUsername(verification.getUser().getUsername());
        dto.setIdCardUrl(verification.getIdCardUrl());
        dto.setLicenseUrl(verification.getLicenseUrl());
        dto.setVehiclePhotoUrl(verification.getVehiclePhotoUrl());
        dto.setStatus(verification.getStatus().toString());
        dto.setVerificationDate(verification.getVerificationDate());
        dto.setAdminComment(verification.getAdminComment());
        return dto;
    }
} 