package com.fredmaina.event_management.controllers;

import com.fredmaina.event_management.DTOs.APIResponse;
import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.EventRepository;
import com.fredmaina.event_management.repositories.UserRepository;
import com.fredmaina.event_management.services.EventService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/events")
@CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    EventService eventService;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Event>> createEvent(@RequestBody EventDto eventDto){
        System.out.println(eventDto.getCreatorId());
        Optional<User> userOptional = userRepository.findById(eventDto.getCreatorId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(false, "Event creation failed. The specified creator ID does not exist.", null)
            );
        }
        Optional<Event> eventOptional = eventService.createEvent(eventDto);
        return eventOptional.map(event -> ResponseEntity.ok(
                new APIResponse<>(true, "Event created successfully", event)
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new APIResponse<>(false, "Event creation failed. Please check your input.", null)
        ));

    }
    @GetMapping("/get/{creator_id}")
    public ResponseEntity<APIResponse<List<Event>>> getEventByCreator(@PathVariable int creator_id){
        Optional<User> optionalUser = userRepository.findById(creator_id);
        if (optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(false,"Error Fetching Events: User with ID: "+creator_id+" does not exist.",null)
            );

        }
        if (eventService.getEventByCreatorId(creator_id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"Error fetching Events: User with id: "+creator_id+" does not have any events created.",null)
            );
        }
        return ResponseEntity.ok(
                new APIResponse<List<Event>>(true,"Events fetched succesfully",eventService.getEventByCreatorId(creator_id).get())
        );
    }
    @GetMapping("/get/")
    public ResponseEntity<APIResponse<List<Event>>> getAllEvents() {
        Optional<List<Event>> optionalEvents = eventService.getAllEvents();
        if (optionalEvents.isPresent()) {
            APIResponse<List<Event>> response = new APIResponse<>(
                    true,
                    "Events Fetched Successfully",
                    optionalEvents.get()
            );
            return ResponseEntity.ok(response);
        } else {
            APIResponse<List<Event>> response = new APIResponse<>(
                    false,
                    "No Events Found",
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse<Event>> deleteEvent(@PathVariable int id){
        Optional<Event> optionalEvent = eventService.getEventById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"Event does not exist",null)
            );
        }
        eventService.deleteEventById(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new APIResponse<>(true,"Event Deleted successfully",null)
        );
    }


}
