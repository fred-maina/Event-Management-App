package com.fredmaina.event_management.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
