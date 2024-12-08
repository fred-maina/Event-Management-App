package com.fredmaina.event_management.controllers;

import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.services.EventService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/events")
public class EventController {
    @Autowired
    EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody EventDto eventDto){
        System.out.println(eventDto.getCreatorId());
        Optional<Event> eventOptional = eventService.createEvent(eventDto);
        if (eventOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventOptional.get());
    }
    @GetMapping("/get/{creator_id}")
    public ResponseEntity<List<Event>> getEventByCreator(@PathVariable int creator_id){
        return ResponseEntity.ok(eventService.geteventByCreatorId(creator_id));
    }
    @GetMapping("/get/")
    public ResponseEntity<List<Event>> getAllEvents(){
        return  ResponseEntity.of(eventService.getAllEvents());
    }


}
