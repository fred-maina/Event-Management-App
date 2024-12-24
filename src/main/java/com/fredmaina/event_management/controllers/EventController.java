package com.fredmaina.event_management.controllers;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import com.fredmaina.event_management.DTOs.APIResponse;
import com.fredmaina.event_management.DTOs.EventDto;
import com.fredmaina.event_management.DTOs.TicketTypeDTO;
import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.User;
import com.fredmaina.event_management.repositories.EventRepository;
import com.fredmaina.event_management.repositories.UserRepository;
import com.fredmaina.event_management.services.EventService;
import com.fredmaina.event_management.services.S3Service;
import com.fredmaina.event_management.services.TicketTypeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/events")
@CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketTypeService ticketTypeService;
    @Autowired
    private S3Service s3Service;

    @PostMapping(value="/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<Event>> createEvent(@RequestPart("event") @Valid EventDto eventDto, @RequestPart("poster") MultipartFile poster){
        System.out.println("Received EventDto: " + eventDto);
        // Replace with your bucket name
        String bucketName = "fredeventsystem";

        // Generate a unique key for the file (use UUID or some unique string)
        String key = UUID.randomUUID()+"_" + poster.getOriginalFilename(); // Or use a UUID to generate a unique filename

        s3Service.uploadFileToS3(bucketName, poster, key);
        String fileUrl = s3Service.getFileUrl(bucketName, key);
        eventDto.setPosterUrl(fileUrl);

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
    public ResponseEntity<APIResponse<List<EventDto>>> getEventsByCreator(@PathVariable UUID creator_id){
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
        List<Event> events =eventService.getEventByCreatorId(creator_id).get();
        List<EventDto> eventDtos = events.stream().map(event -> {return
        new EventDto(
                event.getId(),
                event.getEventName(),
                event.getEventStartDate(),
                event.getEventEndDate(),
                event.getEventVenue(),
                event.getEventCapacity(),
                event.getCreator().getId(),
                event.getPosterUrl(),
                ticketTypeService.findAllTicketTypesByEvent(event).stream().map(ticketType->{
            return new TicketTypeDTO(
                    ticketType.getId(),
                    ticketType.getTypeCategory(),
                    ticketType.getNumberOfTickets(),
                    ticketType.getPrice(),
                    ticketType.getEvent().getId());
        }).toList() );
        }).toList();

        return ResponseEntity.ok(
                new APIResponse<List<EventDto>>(true,"Events fetched successfully",eventDtos)
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
    public ResponseEntity<APIResponse<Event>> deleteEvent(@PathVariable UUID id){
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFileToS3(
            @RequestParam("file") MultipartFile file) {

        // Replace with your bucket name
        String bucketName = "fredeventsystem";

        // Generate a unique key for the file (use UUID or some unique string)
        String key = file.getOriginalFilename(); // Or use a UUID to generate a unique filename

        // Upload the file and get the URL
        s3Service.uploadFileToS3(bucketName, file, key);
        String fileUrl = s3Service.getFileUrl(bucketName, key);

        return ResponseEntity.ok(Map.of("url", fileUrl));
    }

}
