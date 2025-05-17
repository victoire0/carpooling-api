package com.carpooling.api.dto;

import com.carpooling.api.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private String type;
    private UserDTO user;
    private String message;
    private boolean success;
    
    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.type = "Bearer";
        this.user = user;
        this.success = true;
    }
    
    public static AuthResponse success(String token, UserDTO user) {
    AuthResponse response = new AuthResponse();
    response.setToken(token);
    response.setType("Bearer");
    response.setUser(user);
    response.setSuccess(true);
    response.setMessage("Authentication successful");
    return response;
    }
    
    // Méthode statique pour créer une réponse d'erreur
    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    
}