package com.lmelectronica.ecommerce.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;

    public LoginResponse setToken(String token){
        this.token = token;
        return this;
    }

    public LoginResponse setUsername(String username){
        this.username = username;
        return this;
    }
}
