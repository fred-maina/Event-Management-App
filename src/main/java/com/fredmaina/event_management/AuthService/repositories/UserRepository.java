package com.fredmaina.event_management.AuthService.repositories;

import com.fredmaina.event_management.AuthService.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    String email(String email);
}
