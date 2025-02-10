package com.fredmaina.event_management.TicketBookingService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class TicketImageDTO {
    private String eventName;
    private String eventDate;
    private String buyerName;
    private String ticketCode;
    private String paymentMode;
    private String ticketType;
    private int ticketPrice;
    private LocalDateTime purchaseTime;
    private String buyerEmail;
    private String mpesaCode;


}
