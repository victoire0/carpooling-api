package com.carpooling.api.config;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.carpooling.api.dto.AuthResponse;
import com.carpooling.api.service.OAuth2Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Lazy
    private final OAuth2Service oauth2Service;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication == null) {
            response.sendRedirect("/login?error=authentication_failed");
            return;
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2User)) {
            response.sendRedirect("/login?error=invalid_user_type");
            return;
        }
        
        OAuth2User oauth2User = (OAuth2User) principal;
        try {
            AuthResponse authResponse = oauth2Service.processOAuth2User(oauth2User);
            if (authResponse == null || !authResponse.isSuccess()) {
                throw new RuntimeException("OAuth2 processing failed");
            }
            getRedirectStrategy().sendRedirect(request, response, "/home?token=" + authResponse.getToken());
        } catch (Exception e) {
            getRedirectStrategy().sendRedirect(request, response, "/login?error=" + e.getMessage());
        }
    }
}