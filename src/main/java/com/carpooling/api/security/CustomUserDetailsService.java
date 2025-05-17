package com.carpooling.api.security;

import com.carpooling.api.entity.User;
import com.carpooling.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("customUserDetailsService")
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        
        // Convertir l'utilisateur en UserDetails
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // Ajouter les rôles de base
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Ajouter le rôle de conducteur si applicable
        if (user.getIsDriver() != null && user.getIsDriver()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
        }
        
        // Retourner un objet UserDetails à partir de notre User
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),           // Username (email dans ce cas)
            user.getPassword(),        // Mot de passe hashé
            user.getIsActive(),        // Enabled
            true,                      // Account non-expiré
            true,                      // Credentials non-expirées
            true,                      // Account non-locked
            authorities                // Autorisations
        );
    }
}