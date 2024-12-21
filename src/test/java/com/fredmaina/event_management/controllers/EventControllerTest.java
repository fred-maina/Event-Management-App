package com.fredmaina.event_management.controllers;


import com.fredmaina.event_management.DTOs.APIResponse;
import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.DTOs.TicketTypeDTO;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.TicketType;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.UserRepository;
import com.fredmaina.event_management.services.EventService;
import com.fredmaina.event_management.services.TicketTypeService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
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
    @Mock
    private TicketTypeService ticketTypeService;
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
    }@Test
    public void testGetEventsByCreatorID() {
        int creatorId = 1;

        // Mock data
        User mockUser = new User();
        Event mockEvent = new Event();
        mockEvent.setId(1);
        mockEvent.setEventName("Sample Event");
        mockEvent.setEventStartDate(LocalDateTime.now());
        mockEvent.setEventEndDate(LocalDateTime.now().plusHours(2));
        mockEvent.setEventVenue("Sample Venue");
        mockEvent.setEventCapacity(100);
        mockEvent.setCreator(mockUser);

        TicketType mockTicketType = new TicketType();
        mockTicketType.setId(1);
        mockTicketType.setTypeCategory("VIP");
        mockTicketType.setNumberOfTickets(50);
        mockTicketType.setPrice(500);
        mockTicketType.setEvent(mockEvent);

        TicketTypeDTO ticketTypeDto = new TicketTypeDTO(1, "VIP", 50, 500, 1);
        EventDto eventDto = new EventDto(1, "Sample Event", mockEvent.getEventStartDate(),
                mockEvent.getEventEndDate(), "Sample Venue", 100, 1, List.of(ticketTypeDto));

        // Mocking dependencies
        when(userRepository.findById(creatorId)).thenReturn(Optional.of(mockUser));
        when(eventService.getEventByCreatorId(creatorId)).thenReturn(Optional.of(List.of(mockEvent)));
        when(ticketTypeService.findAllTicketTypesByEvent(mockEvent)).thenReturn(List.of(mockTicketType));

        // Act
        ResponseEntity<APIResponse<List<EventDto>>> response = eventController.getEventsByCreator(creatorId);

        // Assert
        assertNotNull(response); // Ensure the response is not null
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode()); // Verify HTTP status code
        assertTrue(response.getBody().isSuccess()); // Verify the success flag
        assertEquals("Events fetched successfully", response.getBody().getMessage()); // Verify the message
        assertEquals(1, response.getBody().getData().size()); // Verify the number of events returned
    // Verify the event DTO content
    }


}
