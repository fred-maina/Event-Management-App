package com.fredmaina.event_management.controllers;


import com.fredmaina.event_management.DTOs.APIResponse;
import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.UserRepository;
import com.fredmaina.event_management.services.EventService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;
    @Mock
    private UserRepository userRepository;
    EventControllerTest(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCreateEventSuccess(){
        EventDto eventDto = new EventDto();
        eventDto.setCreatorId(1);
        Event event = new Event();
        User user = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(eventService.createEvent(eventDto)).thenReturn(Optional.of(event));

        ResponseEntity<APIResponse<Event>> response= eventController.createEvent(eventDto);
        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Event created successfully", response.getBody().getMessage());
        assertEquals(event, response.getBody().getData());
    }
    @Test
    public void testCreateEventFailure_noUser(){
        EventDto eventDto=new EventDto();
        eventDto.setCreatorId(2);
        Event event = new Event();
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<APIResponse<Event>> response = eventController.createEvent(eventDto);
        assertNotNull(response);
        assertFalse(response.getBody().isSuccess());
        assertEquals(response.getBody().getMessage(),"Event creation failed. The specified creator ID does not exist.");
        assertEquals( response.getStatusCode(), HttpStatusCode.valueOf(400));
    }
    @Test
    public void testGetEventsByCreatorID(){
        int creatorId=1;
        List<Event> events = List.of(new Event());
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(eventService.getEventByCreatorId(creatorId)).thenReturn(Optional.of(events));
        ResponseEntity<APIResponse<List<Event>>> response = eventController.getEventByCreator(creatorId);
        assertNotNull(response);
        assertEquals(response.getBody().getData(),events);
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
        assertEquals(response.getBody().getMessage(),"Events fetched successfully");
    }

}
