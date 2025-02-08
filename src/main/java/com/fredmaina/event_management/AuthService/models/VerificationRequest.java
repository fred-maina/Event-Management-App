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
    public  enum VerificationType{
        FORGOT_PASSWORD,
        VERIFY_EMAIL,
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key with auto-increment

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    private VerificationType verificationType;

    private int verificationCode;
    public VerificationRequest(User user, int verificationCode,VerificationType verificationType) {
        this.user = user;
        this.verificationCode = verificationCode;
        this.verificationType = verificationType;
    }
}
