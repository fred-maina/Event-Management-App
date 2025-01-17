package com.fredmaina.event_management.TicketBookingService.DTOs;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackItem {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Value")
    private Object value; // Can be String, Integer, Double, etc.
}