package com.fredmaina.event_management.controllers;

import com.fredmaina.event_management.DTOs.AuthResponse;
import com.fredmaina.event_management.DTOs.RegisterRequest;
import com.fredmaina.event_management.DTOs.LoginRequest;
import com.fredmaina.event_management.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Optional: Allow requests from all origins
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user.
     *
     * @param registerRequest the registration details
     * @return AuthResponse containing user information and a JWT token
     */
    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    /**
     * Authenticate an existing user.
     *
     * @param loginRequest the login credentials
     * @return AuthResponse containing a JWT token if authentication is successful
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }
}
