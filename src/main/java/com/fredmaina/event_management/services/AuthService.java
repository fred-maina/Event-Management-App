package com.fredmaina.event_management.services;

import com.fredmaina.event_management.DTOs.RegisterRequest;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;
    public void registerUser(RegisterRequest registerRequest){
        User user = new User(registerRequest.getFirstName(),registerRequest.getLastName(),registerRequest.getEmail(),registerRequest.getPassword());
        userRepository.save(user);
    }
}
