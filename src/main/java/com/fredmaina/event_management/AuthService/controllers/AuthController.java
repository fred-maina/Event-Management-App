package com.fredmaina.event_management.AuthService.controllers;

import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.services.AuthService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.kms.model.VerifyRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS})

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
    @PostMapping("/verify")
    public AuthResponse verify(@RequestBody int code, @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();
        return authService.verifyUser(code,token);

    }
}
