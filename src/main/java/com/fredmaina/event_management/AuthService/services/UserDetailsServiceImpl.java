package com.fredmaina.event_management.AuthService.services;

import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.fredmaina.event_management.AuthService.models.User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Return a UserDetails object with user information
        return User.builder()
                .disabled(!user.isVerified())
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")  // Assuming all users have a default role of USER
                .build();
    }
}
