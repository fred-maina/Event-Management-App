package com.fredmaina.event_management.AuthService.controllers;

import com.fredmaina.event_management.AWS.services.LambdaService;
import com.fredmaina.event_management.AuthService.DTOs.AuthResponse;
import com.fredmaina.event_management.AuthService.DTOs.RegisterRequest;
import com.fredmaina.event_management.AuthService.DTOs.LoginRequest;
import com.fredmaina.event_management.AuthService.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// Swagger’s RequestBody annotation for documentation
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS}
)
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "Authentication API",
        description = "Handles authentication-related operations such as registration, login, verification, password reset, and account deletion."
)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private LambdaService lambdaService;

    // ============================================================================
    // Registration Endpoint
    // ============================================================================
    @Operation(
            summary = "Register a New User",
            description = "Creates a new user and returns an AuthResponse containing the user's information and a JWT token on success. " +
                    "On error (e.g., HTTP 400), an AuthResponse with an error message is returned without any user or token details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid registration details provided; returns an error message without user or token details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/register")
    public AuthResponse registerUser(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "The registration details for the new user (username, email, and password).",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            RegisterRequest registerRequest) {
        System.out.println(registerRequest);
        return authService.registerUser(registerRequest);
    }

    // ============================================================================
    // Login Endpoint
    // ============================================================================
    @Operation(
            summary = "Authenticate User",
            description = "Authenticates an existing user and returns an AuthResponse containing a JWT token and user information on success. " +
                    "On error (e.g., HTTP 401), an AuthResponse with an error message is returned without any user or token details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials; returns an error message without user or token details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/login")
    public AuthResponse login(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "The login credentials (email and password) for the user.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    // ============================================================================
    // Verification Endpoint
    // ============================================================================
    @Operation(
            summary = "Verify User",
            description = "Verifies a user using a verification code sent to their email. " +
                    "On success, returns an AuthResponse indicating successful verification. " +
                    "On error (e.g., HTTP 400), returns an AuthResponse with an error message."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User verified successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid verification code or email; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/verify")
    public AuthResponse verify(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "A JSON object containing the 'email' and 'verification-code' for user verification.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            Map<String, String> verificationRequest) {
        String email = verificationRequest.get("email");
        String verificationCode = verificationRequest.get("verification-code");
        return authService.verifyUser(verificationCode, email);
    }

    // ============================================================================
    // Delete Account Endpoint
    // ============================================================================
    @Operation(
            summary = "Delete User Account",
            description = "Deletes the authenticated user's account and returns an AuthResponse with a confirmation message. " +
                    "Since the account is deleted, no user details are returned. " +
                    "On error (e.g., HTTP 401), an AuthResponse with an error message is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account deleted successfully; returns a confirmation message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @DeleteMapping("/delete-account")
    public AuthResponse deleteAccount(
            @Parameter(
                    name = "Authorization",
                    in = ParameterIn.HEADER,
                    description = "JWT token of the user in the format 'Bearer <token>'.",
                    required = true
            )
            @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();
        return authService.deleteUser(token);
    }

    // ============================================================================
    // Lambda Test Endpoint
    // ============================================================================
    @Operation(
            summary = "Test Lambda Function",
            description = "Invokes a sample AWS Lambda function and returns its response as plain text."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lambda function invoked successfully",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/lambda-test")
    public String testLambda() {
        return lambdaService.invokeLambda();
    }

    // ============================================================================
    // Forgot Password Endpoint
    // ============================================================================
    @Operation(
            summary = "Forgot Password",
            description = "Sends a password reset verification code to the user's email address. " +
                    "On success, returns an AuthResponse with a success message (no user or token details are included). " +
                    "On error (e.g., HTTP 400), returns an AuthResponse with an error message."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully; returns a confirmation message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email address provided; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/forgot-password")
    public AuthResponse forgotPassword(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "A JSON object containing the user's email address with the key 'email'.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            Map<String, String> forgotPasswordRequest) {
        String email = forgotPasswordRequest.get("email");
        authService.sendForgotPasswordCode(email);
        return new AuthResponse(true, "Verification code has been sent to your email.", null, null);
    }

    // ============================================================================
    // Verify Password Reset Code Endpoint
    // ============================================================================
    @Operation(
            summary = "Verify Password Reset Code",
            description = "Verifies the password reset code sent to the user's email. " +
                    "On success, returns an AuthResponse confirming the verification; " +
                    "on error (e.g., HTTP 400), returns an AuthResponse with an error message."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset code verified successfully; returns a confirmation message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid verification code or email; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/upload-verification-code")
    public AuthResponse uploadVerificationCode(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "A JSON object containing 'email' and 'code' (the reset code) for verification.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            Map<String, String> verificationCodeRequest) {
        String email = verificationCodeRequest.get("email");
        int code = Integer.parseInt(verificationCodeRequest.get("code"));
        return authService.verifyPasswordResetCode(email, code);
    }

    // ============================================================================
    // Reset Password Endpoint
    // ============================================================================
    @Operation(
            summary = "Reset Password",
            description = "Resets the user's password after verifying their identity via a JWT token. " +
                    "On success, returns an AuthResponse confirming the password reset; " +
                    "on error (e.g., HTTP 400 or 401), returns an AuthResponse with an error message without user or token details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully; returns a confirmation message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password or request format; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error; returns an error message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/reset-password")
    public AuthResponse resetPassword(
            @Parameter(
                    name = "Authorization",
                    in = ParameterIn.HEADER,
                    description = "JWT token of the user in the format 'Bearer <token>'.",
                    required = true
            )
            @RequestHeader("Authorization") String token,
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "A JSON object containing the new password with the key 'password'.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            Map<String, String> resetPasswordRequest) {
        token = token.replace("Bearer ", "").trim();
        return authService.resetPassword(token, resetPasswordRequest.get("password"));
    }
}
