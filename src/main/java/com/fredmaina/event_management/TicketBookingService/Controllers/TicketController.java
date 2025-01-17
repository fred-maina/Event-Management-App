package com.fredmaina.event_management.TicketBookingService.Controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fredmaina.event_management.TicketBookingService.DTOs.MpesaCallbackRequest;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentResponseDTO;
import com.fredmaina.event_management.TicketBookingService.DTOs.TicketDTO;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Services.MpesaAuthService;
import com.fredmaina.event_management.TicketBookingService.Services.MpesaSTKPushService;
import com.fredmaina.event_management.TicketBookingService.Services.TicketService;
import com.fredmaina.event_management.globalservices.DTOs.APIResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
@Log4j2
@RestController
@RequestMapping("/api/ticket/")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MpesaAuthService mpesaService;
    @Autowired
    private MpesaAuthService mpesaAuthService;
    @Autowired
    private MpesaSTKPushService mpesaSTKPushService;

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
    @PostMapping("/processMpesaPayment")
    public ResponseEntity<APIResponse<Ticket>> processMpesaPayment(@PathVariable String phoneNumber, @PathVariable String ticketNumber) {
        ticketService.handleMpesaPayment(ticketNumber,phoneNumber);
        return  null;
    }

    @PostMapping("/mpesa/sendSTK")
    public  ResponseEntity<PaymentResponseDTO> sendSTK(@RequestBody Map<String,Object> stkDetails) throws JsonProcessingException {
            String ticketNumber=stkDetails.get("ticketNumber").toString();
            Long phoneNumber=Long.parseLong(stkDetails.get("phoneNumber").toString());

            return mpesaSTKPushService.sendSTKPush(ticketNumber,phoneNumber);

    }

    @PostMapping("mpesa/test")
    public ResponseEntity<String> testSTKPush(@RequestBody MpesaCallbackRequest callbackRequest) {
        System.out.println(callbackRequest);

        return ResponseEntity.ok().build();

    }


}
