package com.carpooling.api.repository;

import com.carpooling.api.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByUserId(Long userId);
} 