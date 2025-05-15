package com.carpooling.api.repository;

import com.carpooling.api.entity.DriverVerification;
import com.carpooling.api.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverVerificationRepository extends JpaRepository<DriverVerification, Long> {
    Optional<DriverVerification> findByUserId(Long userId);
    List<DriverVerification> findByStatus(VerificationStatus status);
    List<DriverVerification> findByStatusOrderByVerificationDateDesc(VerificationStatus status);
    boolean existsByUserId(Long userId);
    long countByStatus(VerificationStatus status);
} 