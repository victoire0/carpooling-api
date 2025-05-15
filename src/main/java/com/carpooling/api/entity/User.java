package com.carpooling.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "welcome_email_sent", nullable = false)
    private boolean welcomeEmailSent = false;
    
    @Column(nullable = false, unique = true, name = "username")
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "is_driver")
    @Builder.Default
    private Boolean isDriver = false;
    
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;
        
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relations avec les entités Driver et Passenger
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Driver driver;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Passenger passenger;
    
    // Méthodes accesseurs pour compatibilité avec le code existant
    public Double getRating() {
        if (Boolean.TRUE.equals(isDriver) && driver != null) {
            return driver.getRating();
        } else if (passenger != null) {
            return passenger.getRating();
        }
        return 0.0;
    }
    
    public void setRating(Double rating) {
        if (Boolean.TRUE.equals(isDriver) && driver != null) {
            driver.setRating(rating);
        }
        if (passenger != null) {
            passenger.setRating(rating);
        }
    }
    
    public Integer getCompletedRides() {
        if (Boolean.TRUE.equals(isDriver) && driver != null) {
            return driver.getCompletedRides();
        } else if (passenger != null) {
            return passenger.getCompletedRides();
        }
        return 0;
    }
    
    public void setCompletedRides(Integer completedRides) {
        if (Boolean.TRUE.equals(isDriver) && driver != null) {
            driver.setCompletedRides(completedRides);
        }
        if (passenger != null) {
            passenger.setCompletedRides(completedRides);
        }
    }
    
    public Integer getTotalRides() {
        if (Boolean.TRUE.equals(isDriver) && driver != null) {
            return driver.getTotalRides();
        } else if (passenger != null) {
            // Pour les passagers, nous utilisons completedRides + cancelledRides
            return passenger.getCompletedRides() + passenger.getCancelledRides();
        }
        return 0;
    }
    
    public void setTotalRides(Integer totalRides) {
        if (Boolean.TRUE.equals(isDriver) && driver != null) {
            driver.setTotalRides(totalRides);
        }
        // Note: pour les passagers, nous ne pouvons pas facilement répartir ce total
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Implémentation des méthodes de UserDetails
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
    return this.email; // Gardez cette implémentation pour Spring Security
    }
    
    // Getter qui préserve le username de l'entity pour la sérialisation
    @JsonIgnore
    public String getLoginUsername() {
    return this.username; // Méthode alternative pour le username réel
}

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.isActive);
    }
    
    // Surcharger les méthodes toString pour éviter les références circulaires
    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", isActive=" + isActive +
            ", isDriver=" + isDriver +
            '}';
    }
}