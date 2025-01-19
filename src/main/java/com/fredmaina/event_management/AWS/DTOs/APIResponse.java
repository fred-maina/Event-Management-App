package com.fredmaina.event_management.AWS.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;
    public APIResponse(Boolean success, String message){
        this.success=success;
        this.message=message;
    }

}
