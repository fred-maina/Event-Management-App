package com.fredmaina.event_management.AuthService.services;

import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.AuthService.utils.JWTUtil;
import com.fredmaina.event_management.AuthService.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse registerUser(RegisterRequest registerRequest) {
        // Check if user with the given email already exists
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(registerRequest.getEmail()));
        if (existingUser.isPresent()) {
            return new AuthResponse(false, "Email already in use", null, null);
        }

        // Encode the password and save the user
        String hashedPassword = PasswordUtil.encodePassword(registerRequest.getPassword());
        User user = new User(registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getEmail(), hashedPassword);

        try {
            userRepository.save(user);
            String token = jwtUtil.generateToken(user.getEmail()); // Generate token using the email
            return new AuthResponse(true, "User registered successfully", user, token);
        } catch (Exception e) {
            return new AuthResponse(false, "Error registering user: " + e.getMessage(), null, null);
        }
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Load user details and generate token
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return new AuthResponse(true, "User authenticated successfully", userRepository.findByEmail(jwtUtil.getUsernameFromToken(token)) , token);
        } catch (Exception e) {
            return new AuthResponse(false, "Invalid credentials: " + e.getMessage(), null, null);
        }
    }

}
