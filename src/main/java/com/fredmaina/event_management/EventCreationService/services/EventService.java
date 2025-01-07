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
