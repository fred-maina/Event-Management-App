package com.fredmaina.event_management.services;

import com.fredmaina.event_management.DTOs.RegisterRequest;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.UserRepository;
import com.fredmaina.event_management.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;
    public Optional<User> registerUser(RegisterRequest registerRequest){
        String hashedPassword = PasswordUtil.encodePassword(registerRequest.getPassword());
        User user = new User(registerRequest.getFirstName(),registerRequest.getLastName(),registerRequest.getEmail(),hashedPassword);
        try {
            userRepository.save(user);
            return Optional.of(user);
        }
        catch (Exception e){
            System.err.println("Error Registering the user: "+e.getMessage());
            return Optional.empty();

        }

    }
}
