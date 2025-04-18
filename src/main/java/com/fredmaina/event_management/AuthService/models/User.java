package com.fredmaina.event_management.AuthService.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id")
    private UUID Id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName ;

    @Column(name="verified")
    private boolean verified;
    
    private String email ;
    @JsonIgnore
    private String password ;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password=password;

    }
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                // Avoid calling Event or TicketType to prevent the cycle.
                '}';
    }


    public boolean isEnabled() {
        return true;
    }
}
