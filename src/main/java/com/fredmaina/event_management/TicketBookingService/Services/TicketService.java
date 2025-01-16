package com.fredmaina.event_management.TicketBookingService.Services;

import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.EventCreationService.services.EventService;
import com.fredmaina.event_management.EventCreationService.services.TicketTypeService;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import com.fredmaina.event_management.TicketBookingService.Repositories.TicketRepository;
import com.fredmaina.event_management.TicketBookingService.Utils.TicketCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void handlePayment(String ticketCode){
        Ticket ticket=ticketRepository.findTicketByTicketCode(ticketCode);
        ticket.setPaymentStatus(Ticket.PaymentStatus.APPROVED);
    }




}
