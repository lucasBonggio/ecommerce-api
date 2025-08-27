package com.lmelectronica.ecommerce.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lmelectronica.ecommerce.shared.dtos.AuthResponse;
import com.lmelectronica.ecommerce.shared.dtos.ChangePasswordRequest;
import com.lmelectronica.ecommerce.shared.dtos.DeleteRequest;
import com.lmelectronica.ecommerce.shared.dtos.LoginRequest;
import com.lmelectronica.ecommerce.shared.dtos.RegisterRequest;
import com.lmelectronica.ecommerce.shared.dtos.UpdateUserDTO;
import com.lmelectronica.ecommerce.shared.dtos.UserDTO;
import com.lmelectronica.ecommerce.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "API for user authentication and registration")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @Operation(summary = "Register new user",
                description = "Creates a new user account and returns authentication token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "User registered successfully.",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400",
                    description = "Registration failed - invalid data or user already exists.",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User registration data", required = true)
            @RequestBody RegisterRequest request) {
        
        AuthResponse response = authService.register(request);
        
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "User login",
                description = "Authenticates user credentials and returns JWT token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Login successful.",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400",
                    description = "Login failed - invalid credentials.",
                    content = @Content)})
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "User login credentials", required = true)
            @RequestBody LoginRequest request) {
        
        AuthResponse response = authService.login(request);
        
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change password",
                description = "Updates user's password with proper validation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Password updated successfully.",
                    content = @Content),
        @ApiResponse(responseCode = "400",
                    description = "Invalid password or validation failed.",
                    content = @Content)})
    @PutMapping("/change-password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Password change request data", required = true)
            @RequestBody ChangePasswordRequest request,

            Authentication authentication) {
        
            String username = authentication.getName();
            userService.updatePassword(username, request);
            return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "Update user profile",
                description = "Updates user's profile information (first name, last name).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Profile updated successfully.",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404",
                    description = "User not found.",
                    content = @Content)})
    @PutMapping("/update-profile")
    public ResponseEntity<UserDTO> updateProfile(
            @Parameter(description = "Profile update data", required = true)
            @RequestBody UpdateUserDTO request, 
            
            Authentication authentication) {
                
            String username = authentication.getName();
            UserDTO user = userService.updateProfile(username, request);
            return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Delete user account",
                description = "Permanently deletes a user account with password confirmation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Account deleted successfully.",
                    content = @Content),
        @ApiResponse(responseCode = "401",
                    description = "Invalid credentials or unauthorized operation.",
                    content = @Content)
    })
    @DeleteMapping("/delete-profile")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account deletion request with credentials", required = true)
            @RequestBody DeleteRequest request,
            
            Authentication authentication) {
                
            String username = authentication.getName();
            userService.deleteAccount(username, request);
            return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get current user",
                description = "Returns current authenticated user information. Requires valid JWT token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "User authenticated successfully.",
                    content = @Content(schema = @Schema(type = "string", example = "User authenticated successfully"))),
        @ApiResponse(responseCode = "401",
                    description = "Unauthorized - invalid or missing token.",
                    content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        return ResponseEntity.ok("User authenticated successfully");
    }
}