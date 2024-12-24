package com.fredmaina.event_management.controllers;

import com.fredmaina.event_management.DTOs.RegisterRequest;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Optional<User>> registerUser(@RequestBody RegisterRequest registerRequest){
    Optional <User> optional= authService.registerUser(registerRequest);
        if (optional.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(optional);
    }


}
