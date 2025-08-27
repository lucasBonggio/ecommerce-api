package com.lmelectronica.ecommerce.shared.dtos;

import com.lmelectronica.ecommerce.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String userName;
    private String email;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
    
}
