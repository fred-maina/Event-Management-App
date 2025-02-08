package com.fredmaina.event_management.EventCreationService.services;

import com.fredmaina.event_management.EventCreationService.DTOs.EventDto;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.EventType;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.EventCreationService.repositories.EventRepository;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.AWS.services.S3Service;
import com.fredmaina.event_management.EventCreationService.repositories.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.*;

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
    @Autowired
    private EventTypeRepository eventTypeRepository;

    public Optional<Event> createEvent(EventDto eventDto) {
        UUID userId = eventDto.getCreatorId();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();


        // Create the event object without capacity
        Event event = new Event(eventDto.getId(), eventDto.getEventName(), eventDto.getEventStartDate(),
                eventDto.getEventEndDate(), eventDto.getEventVenue(), 0, eventDto.getPosterUrl(), user,
                this.getEventTypesById(eventDto.getEventTypeIds()) );


        Set<Long> eventTypeIds = eventDto.getEventTypeIds();

// Loop over each eventTypeId and retrieve the corresponding EventType entity
        Set<EventType> eventTypes = new HashSet<>();
        for (Long eventTypeId : eventTypeIds) {
            // Assuming you have an EventType repository to fetch the EventType by ID
            EventType eventType = eventTypeRepository.findById(eventTypeId)
                    .orElseThrow(() -> new RuntimeException("EventType not found for id: " + eventTypeId));

            // Add the EventType to the set of event types
            eventTypes.add(eventType);
        }


        event.setEventTypes(eventTypes);
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

    public Optional<Page<Event>> getEventByCreatorId(UUID id,int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByCreatorId(id,pageable);

        return events.isEmpty() ? Optional.empty():Optional.of(events);
    }

    public Optional<Page<Event>> getAllEvents(int page,int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return Optional.of(eventRepository.findAll(pageable));
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
         if(eventDto.getEventCapacity()!=null){
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

        if(eventDto.getTicketType() !=null){
            List<TicketType> ticketType= new ArrayList<>();
            eventDto.getTicketType().forEach(ticketTypeDTO -> {

                ticketType.add(ticketTypeService.updateTicketType(ticketTypeDTO.getId(),ticketTypeDTO).get());
            });

        }
    return  Optional.of(eventRepository.save(eventOptional.get()));

    }
    public List<EventType> getAllEventTypes(){
        return eventTypeRepository.findAll();
    }
    public Set<EventType> getEventTypesById(Set<Long> eventTypeIds){
        Set<EventType> eventTypes = new HashSet<>();
        for (Long eventTypeId : eventTypeIds) {
            eventTypeRepository.findById(eventTypeId).ifPresent(eventTypes::add);
        }
        return eventTypes;

    }
    public EventType getEventTypeById(Long id){
        Optional<EventType> eventType = eventTypeRepository.findById(id);
        return eventType.orElse(null);
    }
    public EventType getEventTypeByName(String name){
        Optional<EventType> eventType = eventTypeRepository.findByName(name);
        return eventType.orElse(null);
    }

}
