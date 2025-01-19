package com.fredmaina.event_management.AuthService.repositories;

import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.models.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    Optional<VerificationRequest> findByUser(User user);
}
