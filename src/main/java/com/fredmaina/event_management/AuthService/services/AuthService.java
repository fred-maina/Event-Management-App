package com.fredmaina.event_management.AuthService.services;

import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.models.VerificationRequest;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.AuthService.repositories.VerificationRequestRepository;
import com.fredmaina.event_management.AuthService.utils.GenerateCode;
import com.fredmaina.event_management.AuthService.utils.PasswordUtil;
import com.fredmaina.event_management.Email.Service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;



    @Autowired
    private JWTService jwtService;
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
            String token = jwtService.generateToken(user.getEmail(),"authenticate");
            int code= generateVerificationCode(user.getEmail());// Generate token using the email
            emailService.sendHtmlEmail(registerRequest.getEmail(),code,registerRequest.getFirstName()+" "+registerRequest.getLastName(),"verificationCode");
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
            String token = jwtService.generateToken(userDetails.getUsername(),"authenticate");
            return new AuthResponse(true, "User authenticated successfully", userRepository.findByEmail(jwtService.getUsernameFromToken(token)) , token);
        } catch (Exception e) {

            return new AuthResponse(false, "Invalid credentials: " + e.getMessage(), null, null);
        }
    }

    public AuthResponse verifyUser(String code, String email) {

        User user = userRepository.findByEmail(email);


        if (user == null) {
            return new AuthResponse(false, "User does not exist. Register and try again.", null, null);
        }

        if (user.isVerified()) {
            return new AuthResponse(false, "User is already verified. Please proceed to log in.", null, null);
        }

        Optional<VerificationRequest> optionalRequest = verificationRequestRepository.findByUser(user);

        if (optionalRequest.isEmpty()) {
            return new AuthResponse(false, "No verification request found.", null, null);
        }

        VerificationRequest verificationRequest = optionalRequest.get();

        // Check verification type
        if (verificationRequest.getVerificationType() != VerificationRequest.VerificationType.VERIFY_EMAIL) {
            return new AuthResponse(false, "Invalid verification request type.", null, null);
        }

        // Validate code
        if (verificationRequest.getVerificationCode() != Integer.parseInt(code)) {
            return new AuthResponse(false, "Invalid verification code.", null, null);
        }

        // Mark user as verified and clean up
        user.setVerified(true);
        userRepository.save(user);
        verificationRequestRepository.delete(verificationRequest);

        return new AuthResponse(true, "User verified successfully.", null, null);
    }

    public int generateVerificationCode(String email) {
        int code=GenerateCode.generateCode();
        User user=userRepository.findByEmail(email);
        VerificationRequest verificationRequest = new VerificationRequest(user,code, VerificationRequest.VerificationType.VERIFY_EMAIL);
        verificationRequestRepository.save(verificationRequest);
        return code;
    }
    public AuthResponse deleteUser(String token) {
        User user = userRepository.findByEmail(jwtService.getUsernameFromToken(token));
        userRepository.delete(user);
        return new AuthResponse(true, "User deleted successfully", null, null);

    }
    public int generateForgotPasswordCode(String email){
        // Find the user by email
        User user = userRepository.findByEmail(email);
        // Check if the user exists
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        // Check if there is an existing verification request for the user
        Optional<VerificationRequest> existingRequest = verificationRequestRepository.findByUser(user);
        // If the verification request exists, return the existing code
        if (existingRequest.isPresent()) {
            return existingRequest.get().getVerificationCode();
        }
        // If no existing code, generate a new one
        int code = GenerateCode.generateCode();
        // Create a new VerificationRequest and save it
        VerificationRequest verificationRequest = new VerificationRequest(user, code, VerificationRequest.VerificationType.FORGOT_PASSWORD);
        verificationRequestRepository.save(verificationRequest);
        return code;
    }

    public void sendForgotPasswordCode(String email){
        User user = userRepository.findByEmail(email);
        if(user==null){
            return;
        }
        int code = generateForgotPasswordCode(user.getEmail());
        emailService.sendHtmlEmail(email,code,user.getFirstName()+user.getLastName(),"resetPassword");
    }
    public AuthResponse verifyPasswordResetCode(String email, int code) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new AuthResponse(false, "User does not exist. Register and try again.", null, null);
        }
        Optional<VerificationRequest> optionalRequest = verificationRequestRepository.findByUser(user);
        if (optionalRequest.isEmpty()) {
            return new AuthResponse(false, "Invalid verification code.", null, null);
        }
        VerificationRequest verificationRequest = optionalRequest.get();
        // Validate code and verification type
        if (verificationRequest.getVerificationCode() != code ||
                verificationRequest.getVerificationType() != VerificationRequest.VerificationType.FORGOT_PASSWORD) {
            return new AuthResponse(false, "Invalid verification code.", null, null);
        }
        // Generate token before deleting verification request (in case of errors)
        String resetToken = jwtService.generateToken(user.getEmail(), "reset-password");
        // Remove verification request after successful validation
        verificationRequestRepository.delete(verificationRequest);
        return new AuthResponse(true, "Proceed to set a new password.", user, resetToken);
    }

    public AuthResponse resetPassword(String token,String password) {
        User user = userRepository.findByEmail(jwtService.getUsernameFromToken(token));
        if(user==null){
            return new AuthResponse(false,"Invalid token",null,null);
        }
        if(!jwtService.getClaimFromToken(token,"action").equals("reset-password")){
            return new AuthResponse(false,"Invalid token",null,null);
        }
        user.setPassword(PasswordUtil.encodePassword(password));
        userRepository.save(user);

        return new AuthResponse(true,"Password reset successfully", user, jwtService.generateToken(user.getEmail(),"authenticate"));
    }
}
