package com.fredmaina.event_management.AuthService.services;

import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.models.VerificationRequest;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.AuthService.repositories.VerificationRequestRepository;
import com.fredmaina.event_management.AuthService.utils.GenerateCode;
import com.fredmaina.event_management.AuthService.utils.JWTUtil;
import com.fredmaina.event_management.AuthService.utils.PasswordUtil;
import com.fredmaina.event_management.Email.Service.EmailService;
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
    private VerificationRequestRepository verificationRequestRepository;



    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private EmailService emailService;

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
            String token = jwtUtil.generateToken(user.getEmail());
            int code= generateVerificationCode(user.getEmail());// Generate token using the email
            emailService.sendHtmlEmail(registerRequest.getEmail(),code,registerRequest.getFirstName()+" "+registerRequest.getLastName());
            return new AuthResponse(true, "Check your email for your verification code", user, token);
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

    public AuthResponse verifyUser(int code,String token) {
        User user= userRepository.findByEmail(jwtUtil.getUsernameFromToken(token));
        if (user.isVerified()){return new AuthResponse(false,"user is already verified please proceed to log in",null,null);}
        Optional<VerificationRequest> verificationRequestOptional = verificationRequestRepository.findByUser(user);
        if (verificationRequestOptional.isEmpty()) {
            return new AuthResponse(false,"User does not exist. Register and try Again", null, null);
        }
        VerificationRequest verificationRequest = verificationRequestOptional.get();
        if(verificationRequest.getVerificationCode()==code){
            user.setVerified(true);
            userRepository.save(user);
            verificationRequestRepository.delete(verificationRequest);
            return new AuthResponse(true, "User verified successfully", null, null);
        }
        return new AuthResponse(false, "Invalid verification code", null, null);


    }
    public int generateVerificationCode(String email) {
        int code=GenerateCode.generateCode();
        User user=userRepository.findByEmail(email);
        VerificationRequest verificationRequest = new VerificationRequest(user,code);
        verificationRequestRepository.save(verificationRequest);
        return code;
    }
    public AuthResponse deleteUser(String token) {
        User user = userRepository.findByEmail(jwtUtil.getUsernameFromToken(token));
        userRepository.delete(user);
        return new AuthResponse(true, "User deleted successfully", null, null);

    }
}
