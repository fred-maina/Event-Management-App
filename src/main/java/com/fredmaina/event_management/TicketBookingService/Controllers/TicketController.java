package com.fredmaina.event_management.TicketBookingService.Controllers;


import com.fredmaina.event_management.TicketBookingService.DTOs.TicketDTO;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket/")
public class TicketController {
    @PostMapping("/purchase/")
    public String purchaseTicket(@RequestBody TicketDTO ticketDTO) {
        return null;


    }

}
