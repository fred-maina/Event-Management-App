package com.fredmaina.event_management.TicketBookingService.DTOs;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackBody {

    @JsonProperty("stkCallback")
    private StkCallback stkCallback;
}