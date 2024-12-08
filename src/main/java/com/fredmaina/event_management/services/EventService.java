package com.fredmaina.event_management.services;

import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.DTOs.UserDto;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.EventRepository;
import com.fredmaina.event_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public Optional<Event> createEvent(EventDto eventDto){
        int userId = eventDto.getCreatorId();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()){
            return Optional.empty();
        }
        User user = userOptional.get();
        Event event=new Event(eventDto.getId(),eventDto.getEventName(),eventDto.getEventStartDate(),eventDto.getEventEndDate(),eventDto.getEventVenue(), eventDto.getEventCapacity(), user);
        System.out.println(event);
        eventRepository.save(event);
        return Optional.of(event);
    }
    public List<Event> geteventByCreatorId(int id){
        return eventRepository.findByCreatorId(id);

    }

    public Optional<List<Event>> getAllEvents() {
        return Optional.of(eventRepository.findAll());
    }
}
