package com.carpooling.api.repository;

import com.carpooling.api.entity.Admin;
import com.carpooling.api.entity.Role; // Ajout de l'import manquant
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Ajout de l'import manquant
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Admin> findByRole(Role role);
}