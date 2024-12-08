package com.fredmaina.event_management.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int Id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName ;

    private String email ;
    @JsonIgnore
    private String password ;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.password=password;
    }
}
