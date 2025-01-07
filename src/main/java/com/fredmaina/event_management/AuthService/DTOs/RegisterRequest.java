package com.fredmaina.event_management.AuthService.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class RegisterRequest {
    private String firstName;
    private String lastName ;
    private String email ;
    private String password;
}
