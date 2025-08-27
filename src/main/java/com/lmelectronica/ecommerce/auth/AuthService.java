package com.lmelectronica.ecommerce.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lmelectronica.ecommerce.config.JwtService;
import com.lmelectronica.ecommerce.shared.dtos.AuthResponse;
import com.lmelectronica.ecommerce.shared.dtos.LoginRequest;
import com.lmelectronica.ecommerce.shared.dtos.RegisterRequest;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.Role;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            return new AuthResponse(null, null, null, null, "Username already exists. ");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            return new AuthResponse(null, null, null, null, "Email already exists. ");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole() != null ? request.getRole() : Role.customer);

        User savedUser = userRepository.save(user);
        
        String jwtToken = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(jwtToken, savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }


    public AuthResponse login(LoginRequest request){
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUsername()));

            String jwtToken = jwtService.generateToken(user);
        
            return new AuthResponse(jwtToken, user.getUsername(), user.getEmail(), user.getRole());
    }
    
}
