package com.carpooling.api.config;

import com.carpooling.api.entity.Admin;
import com.carpooling.api.entity.Role;
import com.carpooling.api.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initializeAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Vérifier si un SUPER_ADMIN existe deja
            List<Admin> superAdmins = adminRepository.findByRole(Role.SUPER_ADMIN);
            if (superAdmins.isEmpty()) {
                Admin superAdmin = new Admin();
                superAdmin.setEmail("aminikayembe1@gmail.com");
                superAdmin.setPassword(passwordEncoder.encode("CEO@TujeRide2025"));
                superAdmin.setFirstName("Victoire");
                superAdmin.setLastName("Amini Kayembe");
                superAdmin.setActive(true);
                superAdmin.setCreatedAt(LocalDateTime.now());
                superAdmin.setRole(Role.SUPER_ADMIN);
                
                adminRepository.save(superAdmin);
                
                System.out.println("SUPER_ADMIN créé avec succès.");
            } else {
                System.out.println("Un SUPER_ADMIN existe déjà dans la BD.");
            }
        };
    }
}