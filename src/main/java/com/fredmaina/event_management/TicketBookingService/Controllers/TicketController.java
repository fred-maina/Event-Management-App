package com.fredmaina.event_management.TicketBookingService.Controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.EventCreationService.repositories.TicketTypeRepository;
import com.fredmaina.event_management.TicketBookingService.DTOs.MpesaCallbackRequest;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentResponseDTO;
import com.fredmaina.event_management.TicketBookingService.DTOs.TicketDTO;
import com.fredmaina.event_management.TicketBookingService.Models.MpesaTransaction;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Repositories.MpesaTransactionRepository;
import com.fredmaina.event_management.TicketBookingService.Services.MpesaAuthService;
import com.fredmaina.event_management.TicketBookingService.Services.MpesaSTKPushService;
import com.fredmaina.event_management.TicketBookingService.Services.TicketService;
import com.fredmaina.event_management.AWS.DTOs.APIResponse;
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
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    @Autowired
    private MpesaTransactionRepository mpesaTransactionRepository;

    @PostMapping("/purchase")
    public ResponseEntity<APIResponse<Ticket>> purchaseTicket(@RequestBody TicketDTO ticketDTO) {
        if(eventRepository.findById(ticketDTO.getEventId()).isEmpty()){return ResponseEntity.notFound().build();}

        if(eventRepository.findById(ticketDTO.getEventId()).get().getEventCapacity()<1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"Tickets Are sold Out!!",null)
            );
        }
        if(ticketTypeRepository.findById(ticketDTO.getTicketTypeId()).isEmpty()){return ResponseEntity.notFound().build();}
        if(ticketTypeRepository.findById(ticketDTO.getTicketTypeId()).get().getNumberOfTickets()<1){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>(false,"Tickets sold out!!",null)
        );}
        Optional<Ticket> ticketOptional=ticketService.purchaseTicket(ticketDTO.getTicketTypeId(),ticketDTO.getEventId());
        if (ticketOptional.isPresent()){
            Ticket ticket=ticketOptional.get();
            return ResponseEntity.ok(
                    new APIResponse<>(
                            true,
                            "Ticket Reserved Successfully Please proceed to checkout",
                            ticket
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "Event Not Found",null));
    }


    @PostMapping("/mpesa/sendSTK")
    public  ResponseEntity<PaymentResponseDTO> sendSTK(@RequestBody Map<String,String> stkDetails) throws JsonProcessingException {
            String ticketNumber= stkDetails.get("ticketNumber");
            String phoneNumber=stkDetails.get("phoneNumber");
            PaymentResponseDTO payment= ticketService.handleMpesaPaymentOriginalRequest(ticketNumber,phoneNumber);
            return ResponseEntity.ok(payment);



    }

    @PostMapping("mpesa/test")
    public ResponseEntity<String> testSTKPush(@RequestBody MpesaCallbackRequest callbackRequest) {
        System.out.println(callbackRequest);

        System.out.println(ticketService.completeCheckout(callbackRequest).getTicketStatus());


        return ResponseEntity.ok().build();

    }


}
