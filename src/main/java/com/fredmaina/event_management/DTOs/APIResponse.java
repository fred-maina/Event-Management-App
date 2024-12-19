package com.fredmaina.event_management.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;

@Getter
@Setter
@AllArgsConstructor
public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;
    APIResponse(Boolean success,String message){
        this.success=success;
        this.message=message;
    }

}
