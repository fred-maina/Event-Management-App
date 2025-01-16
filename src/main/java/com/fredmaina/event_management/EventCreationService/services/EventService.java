package com.fredmaina.event_management.EventCreationService.services;

import com.fredmaina.event_management.EventCreationService.DTOs.EventDto;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.globalservices.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private TicketTypeService ticketTypeService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3Service;

    public Optional<Event> createEvent(EventDto eventDto) {
        UUID userId = eventDto.getCreatorId();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();

        // Create the event object without capacity
        Event event = new Event(eventDto.getId(), eventDto.getEventName(), eventDto.getEventStartDate(),
                eventDto.getEventEndDate(), eventDto.getEventVenue(), 0, eventDto.getPosterUrl(), user);

        // Convert TicketTypeDTOs to TicketType entities
        List<TicketType> ticketTypes = eventDto.getTicketType().stream().map(ticketTypeDTO -> {
            TicketType ticketType = new TicketType();
            ticketType.setTypeCategory(ticketTypeDTO.getTypeCategory());
            ticketType.setNumberOfTickets(ticketTypeDTO.getNumberOfTickets());
            ticketType.setPrice(ticketTypeDTO.getPrice());
            ticketType.setEvent(event);
            return ticketType;
        }).toList();

        // Calculate event capacity before saving the event
        int calculatedCapacity = calculateCapacity(ticketTypes);
        event.setEventCapacity(calculatedCapacity);

        // Save the event
        eventRepository.save(event);

        // Save ticket types
        ticketTypeService.createTicketType(ticketTypes);

        return Optional.of(event);
    }

    private int calculateCapacity(List<TicketType> ticketTypes) {
        // Check if any ticket type has -1 for the number of tickets (unlimited capacity)
        for (TicketType ticketType : ticketTypes) {
            if (ticketType.getNumberOfTickets() == -1) {
                return -1;  // Unlimited capacity
            }
        }

        // Sum up the number of tickets for all ticket types
        return ticketTypes.stream()
                .mapToInt(TicketType::getNumberOfTickets)
                .sum();
    }

    public Optional<List<Event>> getEventByCreatorId(UUID id){
        List<Event> events= eventRepository.findByCreatorId(id);

        return events.isEmpty() ? Optional.empty():Optional.of(events);
    }

    public Optional<List<Event>> getAllEvents()
    {
        return Optional.of(eventRepository.findAll());
    }
    public Optional<Event> getEventById(UUID id){
        return eventRepository.findById(id);
    }
    public void  deleteEventById(UUID id){
        String bucketName = "fredeventsystem";
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()){
            eventOptional.map(event->event.getPosterUrl()).ifPresent(posterURL-> {
                try {
                    s3Service.deleteFileFromURL(bucketName,posterURL);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        eventRepository.deleteById(id);
    }

public Optional<Event> updateEventInfo(EventDto eventDto,UUID id){
        Optional<Event> eventOptional = eventRepository.findById(id);
        if(eventOptional.isEmpty()){
            return Optional.empty();
        }

        if(eventDto.getEventVenue()!=null){
            eventOptional.get().setEventVenue(eventDto.getEventVenue());
        }
        if(eventDto.getEventName()!=null){
            eventOptional.get().setEventName(eventDto.getEventName());
        }
         if(eventDto.getEventCapacity()!=0){
            eventOptional.get().setEventCapacity(eventDto.getEventCapacity());
        }
        if(eventDto.getEventStartDate()!=null){
            eventOptional.get().setEventStartDate(eventDto.getEventStartDate());
        }
        if(eventDto.getEventEndDate()!=null){
            eventOptional.get().setEventEndDate(eventDto.getEventEndDate());
        }
        if(eventDto.getPosterUrl()!=null){
            eventOptional.get().setPosterUrl(eventDto.getPosterUrl());
        }
        if(eventDto.getEventVenue()!=null){
            eventOptional.get().setEventEndDate(eventDto.getEventEndDate());
        }
        if(eventDto.getTicketType() !=null){
            List<TicketType> ticketType= new ArrayList<>();
            eventDto.getTicketType().forEach(ticketTypeDTO -> {

                ticketType.add(ticketTypeService.updateTicketType(ticketTypeDTO.getId(),ticketTypeDTO).get());
            });

        }
    return  Optional.of(eventRepository.save(eventOptional.get()));

    }

}
