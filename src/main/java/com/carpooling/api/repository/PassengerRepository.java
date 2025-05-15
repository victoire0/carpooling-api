package com.carpooling.api.repository;

import com.carpooling.api.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    // Méthodes personnalisées si nécessaire
} 