package com.fredmaina.event_management.AuthService.controllers;

import com.fredmaina.event_management.AWS.services.LambdaService;
import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS})
@Tag(name = "Authentication API", description = "Handles all authentication-related operations, such as registration, login, verification, and password management.")
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
    @Operation(summary = "Register a New User", description = "Creates a new user and returns the user's information along with an authentication token.")
    @PostMapping("/register")
    public AuthResponse registerUser(
            @RequestBody @Parameter(description = "The registration details, including username, email, and password.") RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    /**
     * Authenticate an existing user.
     *
     * @param loginRequest the login credentials
     * @return AuthResponse containing a JWT token if authentication is successful
     */
    @Operation(summary = "Authenticate User", description = "Authenticates an existing user and returns an authentication token.")
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody @Parameter(description = "The login credentials, including email and password.") LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    /**
     * Verify a user using a verification code.
     *
     * @param verificationRequest the verification code details
     * @param token the JWT token from the user
     * @return AuthResponse containing the verification status
     */
    @Operation(summary = "Verify User", description = "Verifies a user using a verification code sent to their email.")
    @PostMapping("/verify")
    public AuthResponse verify(
            @RequestBody @Parameter(description = "The verification code details.") Map<String, Integer> verificationRequest,
            @RequestHeader("Authorization") @Parameter(description = "JWT token of the user.") String token) {
        token = token.replace("Bearer ", "").trim();
        return authService.verifyUser(verificationRequest.get("verification-code"), token);
    }

    /**
     * Delete a user account.
     *
     * @param token the JWT token from the user
     * @return AuthResponse indicating the status of the deletion
     */
    @Operation(summary = "Delete User Account", description = "Deletes the authenticated user's account.")
    @DeleteMapping("/delete-account")
    public AuthResponse deleteAccount(
            @RequestHeader("Authorization") @Parameter(description = "JWT token of the user.") String token) {
        token = token.replace("Bearer ", "").trim();
        return authService.deleteUser(token);
    }

    /**
     * Test Lambda Function.
     *
     * @return A response from the Lambda function
     */
    @Operation(summary = "Test Lambda Function", description = "Invokes a sample AWS Lambda function and returns its response.")
    @GetMapping("/lambda-test")
    public String testLambda() {
        return lambdaService.invokeLambda();
    }

    /**
     * Send a password reset code to the user's email.
     *
     * @param forgotPasswordRequest the user's email
     * @return AuthResponse indicating the status of the operation
     */
    @Operation(summary = "Forgot Password", description = "Sends a password reset verification code to the user's email.")
    @PostMapping("/forgot-password")
    public AuthResponse forgotPassword(
            @RequestBody @Parameter(description = "The user's email address.") Map<String, String> forgotPasswordRequest) {
        String email = forgotPasswordRequest.get("email");
        authService.sendForgotPasswordCode(email);
        return new AuthResponse(true, "Verification code has been sent to your email.", null, null);
    }

    /**
     * Verify a password reset code.
     *
     * @param verificationCodeRequest the verification details (email and code)
     * @return AuthResponse indicating whether the code is valid
     */
    @Operation(summary = "Verify Password Reset Code", description = "Verifies the password reset code sent to the user's email.")
    @PostMapping("/upload-verification-code")
    public AuthResponse uploadVerificationCode(
            @RequestBody @Parameter(description = "The verification details, including email and reset code.") Map<String, String> verificationCodeRequest) {
        return authService.verifyPasswordResetCode(
                verificationCodeRequest.get("email"),
                Integer.parseInt(verificationCodeRequest.get("code")));
    }

    /**
     * Reset the user's password.
     *
     * @param token the JWT token from the user
     * @param resetPasswordRequest the new password details
     * @return AuthResponse indicating the status of the operation
     */
    @Operation(summary = "Reset Password", description = "Resets the user's password after verifying their identity.")
    @PostMapping("/reset-password")
    public AuthResponse resetPassword(
            @RequestHeader("Authorization") @Parameter(description = "JWT token of the user.") String token,
            @RequestBody @Parameter(description = "The new password details.") Map<String, String> resetPasswordRequest) {
        token = token.replace("Bearer ", "").trim();
        return authService.resetPassword(token, resetPasswordRequest.get("password"));
    }
}
