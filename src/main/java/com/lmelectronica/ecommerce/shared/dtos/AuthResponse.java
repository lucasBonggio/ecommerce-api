package com.lmelectronica.ecommerce.shared.dtos;

import com.lmelectronica.ecommerce.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private Role role;
    private String message;

    public AuthResponse(String token, String username, String email, Role role){
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public AuthResponse(String message){
        this.message = message;
    }
}
