package com.fredmaina.event_management.TicketBookingService.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fredmaina.event_management.AWS.services.LambdaService;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.EventCreationService.repositories.TicketTypeRepository;
import com.fredmaina.event_management.EventCreationService.services.EventService;
import com.fredmaina.event_management.EventCreationService.services.TicketTypeService;
import com.fredmaina.event_management.TicketBookingService.DTOs.MpesaCallbackRequest;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentResponseDTO;
import com.fredmaina.event_management.TicketBookingService.DTOs.TicketImageDTO;
import com.fredmaina.event_management.TicketBookingService.Models.MpesaTransaction;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Repositories.MpesaTransactionRepository;
import com.fredmaina.event_management.TicketBookingService.Repositories.TicketRepository;
import com.fredmaina.event_management.TicketBookingService.Utils.TicketCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Timestamp;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TicketService {
    @Autowired
    LambdaService lambdaService;
    @Autowired
    TicketTypeService ticketTypeService;
    @Autowired
    EventService eventService;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    private MpesaSTKPushService mpesaSTKPushService;
    @Autowired
    private MpesaTransactionRepository mpesaTransactionRepository;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;


    @Transactional
    public Optional<Ticket> purchaseTicket(UUID ticketTypeID, UUID eventID, User user) {
        // Retrieve ticket type and event
        Optional<TicketType> ticketType = ticketTypeService.findTicketTypeById(ticketTypeID);
        Optional<Event> event = eventService.getEventById(eventID);

        // Handle case where either ticket type or event is not found
        if (ticketType.isEmpty()) {
            // Optionally handle this case more gracefully by returning an error message or specific response
            return Optional.empty();
        }

        if (event.isEmpty()) {
            // Optionally handle this case more gracefully by returning an error message or specific response
            return Optional.empty();
        }

        // Ensure ticket code is unique
        String ticketCode;
        boolean isUnique;
        do {
            ticketCode = TicketCodeGenerator.generateTicketCode(event.get().getEventName());
            isUnique = !ticketRepository.existsByTicketCode(ticketCode);
        } while (!isUnique);


        // Create and set ticket properties
        Ticket ticket = new Ticket();
        ticket.setTicketType(ticketType.get());
        ticket.setEvent(event.get());
        ticket.setTicketCode(ticketCode);
        ticket.setUser(user);
        System.out.println(ticket.getTicketCode()+" "+ticket.getTicketStatus());

        // Save the ticket to the repository
        ticketRepository.save(ticket);

        // Update the event's capacity and the ticket type's remaining number of tickets
        event.get().setEventCapacity(event.get().getEventCapacity() - 1);
        ticketType.get().setNumberOfTickets(ticketType.get().getNumberOfTickets() - 1);

        // Save the updated event and ticket type to the repository
        eventRepository.save(event.get());
        ticketTypeRepository.save(ticketType.get());

        return Optional.of(ticket); // Return the created ticket
    }
    public PaymentResponseDTO handleMpesaPaymentOriginalRequest(String ticketCode, String phoneNumber) throws JsonProcessingException {
        Optional<Ticket> ticketOptional=ticketRepository.findTicketByTicketCode(ticketCode);
        if(ticketOptional.isEmpty()){
            return null;
        }
        int amount =ticketOptional.get().getTicketType().getPrice();

       PaymentResponseDTO paymentResponseDTO= mpesaSTKPushService.sendSTKPush(ticketCode,Long.parseLong(phoneNumber),amount).getBody();
       MpesaTransaction mpesaTransaction=new MpesaTransaction();
       mpesaTransaction.setAmount(amount);
       mpesaTransaction.setTicketCode(ticketCode);
        assert paymentResponseDTO != null;
        mpesaTransaction.setMerchantRequestId(paymentResponseDTO.getMerchantRequestID());
       mpesaTransaction.setCheckoutRequestId(paymentResponseDTO.getCheckoutRequestID());
       mpesaTransaction.setSuccessful(false);
       mpesaTransaction.setTransactionDate(LocalDateTime.now());
       mpesaTransactionRepository.save(mpesaTransaction);
       return paymentResponseDTO;

    }

    @Transactional
    public Ticket completeCheckout(MpesaCallbackRequest mpesaCallbackRequest) {
        String checkoutRequestId = mpesaCallbackRequest.getBody().getStkCallback().getCheckoutRequestID();
        MpesaTransaction transaction = mpesaTransactionRepository.getMpesaTransactionByCheckoutRequestId(checkoutRequestId);

        // Handle case where the transaction is not found
        if (transaction == null) {
            // Log and return early
            log.error("Transaction not found for CheckoutRequestId: {}", checkoutRequestId);
            return null;
        }

        Ticket ticket = ticketRepository.findTicketByTicketCode(transaction.getTicketCode()).orElse(null);

        // Handle case where the ticket is not found
        if (ticket == null) {
            log.error("Ticket not found for TicketCode: {}", transaction.getTicketCode());
            return null;
        }

        if (mpesaCallbackRequest.getBody().getStkCallback().getResultCode() == 0) {
            ticket.setPaymentStatus(Ticket.PaymentStatus.APPROVED);
            transaction.setSuccessful(true);
            transaction.setTransactionCode(
                    mpesaCallbackRequest.getBody()
                            .getStkCallback()
                            .getCallbackMetadata()
                            .getItems()
                            .stream()
                            .filter(item -> "MpesaReceiptNumber".equals(item.getName()))
                            .map(item -> item.getValue().toString())
                            .findFirst()
                            .orElse(null)
            );

            // Avoid lazy loading; map directly
            String eventName = ticket.getEvent() != null ? ticket.getEvent().getEventName() : "Unknown Event";
            String eventStartDate = ticket.getEvent() != null ? ticket.getEvent().getEventStartDate().toString() : "Unknown Date";
            String buyerName = ticket.getUser() != null ? ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName() : "Unknown Buyer";
            String buyerEmail = ticket.getUser() != null ? ticket.getUser().getEmail() : "Unknown Email";

            TicketImageDTO ticketDTO = new TicketImageDTO(
                    eventName, eventStartDate, buyerName, ticket.getTicketCode(),
                    "M-Pesa", ticket.getTicketType().getTypeCategory().toString(), transaction.getAmount(),
                    transaction.getTransactionDate(), buyerEmail, transaction.getTicketCode()
            );

            // Create the Lambda payload
            Map<String, Object> lambdaPayload = new HashMap<>();
            lambdaPayload.put("event_name", ticketDTO.getEventName());
            lambdaPayload.put("event_date", ticketDTO.getEventDate());
            lambdaPayload.put("buyer_name", ticketDTO.getBuyerName());
            lambdaPayload.put("ticket_code", ticketDTO.getTicketCode());
            lambdaPayload.put("payment_mode", ticketDTO.getPaymentMode());
            lambdaPayload.put("ticket_type", ticketDTO.getTicketType());
            lambdaPayload.put("ticket_price", ticketDTO.getTicketPrice());
            lambdaPayload.put("purchase_time", ticketDTO.getPurchaseTime().toString());
            lambdaPayload.put("to_email", ticketDTO.getBuyerEmail());
            lambdaPayload.put("mpesa_code", ticketDTO.getMpesaCode());

            try {
                ticket.setUrl(lambdaService.invokeLambda(lambdaPayload));
            } catch (Exception e) {
                log.error("Error invoking Lambda function: ", e);
                ticket.setUrl("Error invoking Lambda");
            }

            // Save updated ticket and transaction
            ticketRepository.save(ticket);
            mpesaTransactionRepository.save(transaction);

            return ticket;
        }

        // Return null or throw exception if the resultCode is not 0
        return null;
    }



}






