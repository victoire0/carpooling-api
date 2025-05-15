package com.carpooling.api.repository;

import com.carpooling.api.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
} 