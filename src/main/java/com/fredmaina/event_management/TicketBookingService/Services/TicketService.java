package com.fredmaina.event_management.TicketBookingService.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.EventCreationService.services.EventService;
import com.fredmaina.event_management.EventCreationService.services.TicketTypeService;
import com.fredmaina.event_management.TicketBookingService.DTOs.MpesaCallbackRequest;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentResponseDTO;
import com.fredmaina.event_management.TicketBookingService.Models.MpesaTransaction;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Repositories.MpesaTransactionRepository;
import com.fredmaina.event_management.TicketBookingService.Repositories.TicketRepository;
import com.fredmaina.event_management.TicketBookingService.Utils.TicketCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Timestamp;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {
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


    public Optional<Ticket> purchaseTicket(UUID ticketTypeID,UUID eventID){
        Optional<TicketType>ticketType=ticketTypeService.findTicketTypeById(ticketTypeID);
        Optional<Event> event = eventService.getEventById(eventID);
        if(ticketType.isEmpty() && event.isEmpty()){
            return Optional.empty();
        }
        Ticket ticket=new Ticket();
        ticket.setTicketType(ticketType.get());
        ticket.setEvent(event.get());
        ticket.setTicketCode(TicketCodeGenerator.generateTicketCode(event.get().getEventName()));

        ticketRepository.save(ticket);
        event.get().setEventCapacity(event.get().getEventCapacity()-1);
        ticketType.get().setNumberOfTickets(ticketType.get().getNumberOfTickets()-1);
        ticketRepository.save(ticket);
        eventRepository.save(event.get());
        return Optional.of(ticket);
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
    public Ticket completeCheckout(MpesaCallbackRequest mpesaCallbackRequest){
        String checkoutRequestId = mpesaCallbackRequest.getBody().getStkCallback().getCheckoutRequestID();
        MpesaTransaction transaction = mpesaTransactionRepository.getMpesaTransactionByCheckoutRequestId(checkoutRequestId);
        Ticket ticket = ticketRepository.findTicketByTicketCode(transaction.getTicketCode()) .get();
        if (mpesaCallbackRequest.getBody().getStkCallback().getResultCode()==0){
            ticket.setPaymentStatus(Ticket.PaymentStatus.APPROVED);
            transaction.setSuccessful(true);
            transaction.setTransactionCode(
                    mpesaCallbackRequest.getBody()
                            .getStkCallback()
                            .getCallbackMetadata()
                            .getItems()
                            .stream()
                            .filter(item -> "MpesaReceiptNumber".equals(item.getName())) // Check if Name is MpesaReceiptNumber
                            .map(item -> item.getValue().toString()) // Map to the Value as a String
                            .findFirst() // Get the first matching item
                            .orElse(null) // Return null if not found
            );
            ticketRepository.save(ticket);
            mpesaTransactionRepository.save(transaction);
            return ticket;
        }
        return null;

    }





}
