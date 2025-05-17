package com.carpooling.api.dto;

import com.carpooling.api.entity.Role;
import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
}