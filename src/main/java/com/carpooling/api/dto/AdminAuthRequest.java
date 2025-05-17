package com.carpooling.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAuthRequest {
    private String email;
    private String password;
} 