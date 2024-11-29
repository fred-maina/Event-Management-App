package com.fredmaina.event_management.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private int userId;
    private String firstName;
    private String lastName ;
    private String email ;
}
