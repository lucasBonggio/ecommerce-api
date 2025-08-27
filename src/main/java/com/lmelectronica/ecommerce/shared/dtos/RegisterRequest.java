package com.lmelectronica.ecommerce.shared.dtos;

import com.lmelectronica.ecommerce.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private Role role = Role.customer;
}
