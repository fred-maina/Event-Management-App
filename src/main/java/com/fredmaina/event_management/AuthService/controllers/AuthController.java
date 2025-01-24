package com.fredmaina.event_management.AuthService.controllers;

import com.fredmaina.event_management.AWS.services.LambdaService;
import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.services.AuthService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.chime.model.DeleteAccountRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.kms.model.VerifyRequest;
import software.amazon.awssdk.services.workmail.model.ResetPasswordRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS})

public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private LambdaService lambdaService;

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
    public AuthResponse verify(@RequestBody Map<String,Integer> verificationRequest, @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();

        return authService.verifyUser(verificationRequest.get("verification-code"),token);
    }
    @DeleteMapping("/delete-account")
    public AuthResponse deleteAccount(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();
        return authService.deleteUser(token);
    }
    @GetMapping("/lambda-test")
    public String testLambda() {
        return lambdaService.invokeLambda();
    }
    @PostMapping("/forgot-password")
    public AuthResponse forgotPassword(@RequestBody Map<String,String> forgotPasswordRequest) {
        String email = forgotPasswordRequest.get("email");
        authService.sendForgotPasswordCode(email);
        return new AuthResponse(true,"verification code has been sent to your email",null,null);
    }
    @PostMapping("/upload-verification-code")
    public AuthResponse uploadVerificationCode(@RequestBody Map<String,String> verificationCodeRequest) {
        return authService.verifyPasswordResetCode(verificationCodeRequest.get("email"), Integer.parseInt(verificationCodeRequest.get("code")));
    }
    @PostMapping("reset-password")
    public AuthResponse resetPassword(@RequestHeader("Authorization") String token
            ,@RequestBody Map<String,String> resetPasswordRequest) {
        token = token.replace("Bearer ", "").trim();
        return authService.resetPassword(token,resetPasswordRequest.get("password"));

    }



}
