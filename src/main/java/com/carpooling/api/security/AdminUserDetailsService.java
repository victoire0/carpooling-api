package com.carpooling.api.security;

import com.carpooling.api.entity.Admin;
import com.carpooling.api.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("adminUserDetailsService")
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin non trouvé avec l'email : " + email));

        // Créer les autorités basées sur le rôle
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + admin.getRole().name());

        // Retourner un UserDetails avec les autorités appropriées
        return new User(
                admin.getEmail(),
                admin.getPassword(),
                admin.isActive(),
                true, // account non-expired
                true, // credentials non-expired
                true, // account non-locked
                Collections.singleton(authority)
        );
    }
}