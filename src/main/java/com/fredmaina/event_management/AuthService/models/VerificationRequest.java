package com.fredmaina.event_management.AuthService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key with auto-increment

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    private int verificationCode;
    public VerificationRequest(User user, int verificationCode) {
        this.user = user;
        this.verificationCode = verificationCode;
    }
}
