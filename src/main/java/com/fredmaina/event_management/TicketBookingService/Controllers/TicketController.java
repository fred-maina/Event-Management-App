package com.fredmaina.event_management.TicketBookingService.Controllers;


import com.fredmaina.event_management.TicketBookingService.DTOs.TicketDTO;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Services.TicketService;
import com.fredmaina.event_management.globalservices.DTOs.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/ticket/")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @PostMapping("/purchase/")
    public ResponseEntity<APIResponse<Ticket>> purchaseTicket(@RequestBody TicketDTO ticketDTO) {
        Optional<Ticket> ticketOptional=ticketService.purchaseTicket(ticketDTO.getTicketTypeId(),ticketDTO.getEventId());
        if (ticketOptional.isPresent()){
            Ticket ticket=ticketOptional.get();
            return ResponseEntity.ok(
                    new APIResponse<>(
                            true,
                            "Ticket Purchased Successfully",
                            ticket
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "Event Not Found",null));




    }

}
