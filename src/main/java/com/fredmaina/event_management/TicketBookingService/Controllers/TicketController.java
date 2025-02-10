package com.fredmaina.event_management.TicketBookingService.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fredmaina.event_management.AWS.services.LambdaService;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.AuthService.services.JWTService;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.EventCreationService.repositories.TicketTypeRepository;
import com.fredmaina.event_management.TicketBookingService.DTOs.MpesaCallbackRequest;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentResponseDTO;
import com.fredmaina.event_management.TicketBookingService.DTOs.TicketDTO;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Repositories.MpesaTransactionRepository;
import com.fredmaina.event_management.TicketBookingService.Services.MpesaAuthService;
import com.fredmaina.event_management.TicketBookingService.Services.MpesaSTKPushService;
import com.fredmaina.event_management.TicketBookingService.Services.TicketService;
import com.fredmaina.event_management.AWS.DTOs.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "3. Ticket API", description = "Handles ticket purchasing, M-Pesa payments, and related operations. Users first reserve a ticket, specifying the event and ticket type. Once reserved, they proceed to checkout via M-Pesa. After successful payment, the ticket is sent via email using AWS Lambda.")
public class TicketController {
    @Autowired private LambdaService lambdaService;
    @Autowired private TicketService ticketService;
    @Autowired private MpesaAuthService mpesaService;
    @Autowired private MpesaAuthService mpesaAuthService;
    @Autowired private MpesaSTKPushService mpesaSTKPushService;
    @Autowired private EventRepository eventRepository;
    @Autowired private TicketTypeRepository ticketTypeRepository;
    @Autowired private MpesaTransactionRepository mpesaTransactionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JWTService jwtService;

    @Operation(summary = "Purchase Ticket", description = "Users reserve a ticket for an event by providing an event ID and a ticket type. After reservation, they receive a ticket code, which is required for payment.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket reserved successfully. Proceed to checkout."),
            @ApiResponse(responseCode = "404", description = "Event or Ticket Type not found")
    })
    @PostMapping("/purchase")
    public ResponseEntity<APIResponse<Ticket>> purchaseTicket(@RequestBody TicketDTO ticketDTO, @RequestHeader("Authorization") String token) {
        String email = jwtService.getUsernameFromToken(token.replace("Bearer ", "").trim());
        User user = userRepository.findByEmail(email);

        if (eventRepository.findById(ticketDTO.getEventId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (eventRepository.findById(ticketDTO.getEventId()).get().getEventCapacity() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "Tickets Are Sold Out!", null));
        }
        if (ticketTypeRepository.findById(ticketDTO.getTicketTypeId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (ticketTypeRepository.findById(ticketDTO.getTicketTypeId()).get().getNumberOfTickets() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "Tickets Sold Out!", null));
        }

        Optional<Ticket> ticketOptional = ticketService.purchaseTicket(ticketDTO.getTicketTypeId(), ticketDTO.getEventId(), user);
        return ticketOptional.map(ticket -> ResponseEntity.ok(new APIResponse<>(true, "Ticket Reserved Successfully. Proceed to checkout", ticket)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false, "Event Not Found", null)));
    }

    @Operation(summary = "Initiate M-Pesa STK Push", description = "Users enter their ticket code and phone number for M-Pesa payment. After successful processing, the ticket is sent to their email.")
    @PostMapping("/mpesa/sendSTK")
    public ResponseEntity<PaymentResponseDTO> sendSTK(@RequestBody Map<String, String> stkDetails) throws JsonProcessingException {
        String ticketNumber = stkDetails.get("ticketNumber");
        String phoneNumber = stkDetails.get("phoneNumber");
        PaymentResponseDTO payment = ticketService.handleMpesaPaymentOriginalRequest(ticketNumber, phoneNumber);
        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "M-Pesa Payment Callback", description = "Handles M-Pesa STK Push callbacks. Once payment is confirmed, the ticket is sent via AWS Lambda to the user's email.")
    @PostMapping("mpesa/callback")
    public ResponseEntity<String> processMpesaCallback(@RequestBody MpesaCallbackRequest callbackRequest) {
        System.out.println(callbackRequest);
        ticketService.completeCheckout(callbackRequest).getTicketStatus();

        return ResponseEntity.ok().build();
    }
}