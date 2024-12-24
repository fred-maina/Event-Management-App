package com.fredmaina.event_management.DTOs;

import com.fredmaina.event_management.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private boolean success;
    private String message;
    private User user;
    private String token;

    public AuthResponse(boolean success, String message, User user, String token) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.token = token;
    }

    // Getters and Setters
}
