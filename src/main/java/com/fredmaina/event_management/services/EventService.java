package com.fredmaina.event_management.services;

import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.DTOs.UserDto;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.TicketType;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.EventRepository;
import com.fredmaina.event_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private TicketTypeService ticketTypeService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public Optional<Event> createEvent(EventDto eventDto){
        UUID userId = eventDto.getCreatorId();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()){
            return Optional.empty();
        }
        User user = userOptional.get();
        Event event=new Event(eventDto.getId(),eventDto.getEventName(),eventDto.getEventStartDate(),eventDto.getEventEndDate(),eventDto.getEventVenue(), eventDto.getEventCapacity(),eventDto.getPosterUrl(), user);
        eventRepository.save(event);
        List<TicketType> ticketTypes = eventDto.getTicketType().stream().map(ticketTypeDTO -> {
            TicketType ticketType=new TicketType();

            ticketType.setTypeCategory(ticketTypeDTO.getTypeCategory());
            ticketType.setNumberOfTickets(ticketTypeDTO.getNumberOfTickets());
            ticketType.setPrice(ticketTypeDTO.getPrice());
            ticketType.setEvent(event);
            System.out.println(ticketType);
            return ticketType;
        }).toList();
        ticketTypeService.createTicketType(ticketTypes);

        return Optional.of(event);
    }
    public Optional<List<Event>> getEventByCreatorId(UUID id){
        List<Event> events= eventRepository.findByCreatorId(id);

        return events.isEmpty() ? Optional.empty():Optional.of(events);
    }

    public Optional<List<Event>> getAllEvents() {
        return Optional.of(eventRepository.findAll());
    }
    public Optional<Event> getEventById(UUID id){
        return eventRepository.findById(id);
    }
    public void  deleteEventById(UUID id){
        eventRepository.deleteById(id);
    }
}
