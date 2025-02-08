package com.fredmaina.event_management.EventCreationService.Contollers;

import com.fredmaina.event_management.AuthService.services.JWTService;
import com.fredmaina.event_management.Email.Service.EmailService;
import com.fredmaina.event_management.EventCreationService.Models.EventType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import com.fredmaina.event_management.AWS.DTOs.APIResponse;
import com.fredmaina.event_management.EventCreationService.DTOs.EventDto;
import com.fredmaina.event_management.EventCreationService.DTOs.TicketTypeDTO;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.EventCreationService.services.EventService;
import com.fredmaina.event_management.AWS.services.S3Service;
import com.fredmaina.event_management.EventCreationService.services.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketTypeService ticketTypeService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private EmailService emailService;



    @PostMapping(value="/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<Event>> createEvent(@RequestPart("event") @Valid EventDto eventDto, @RequestPart("poster") MultipartFile poster,@RequestHeader("Authorization") String token){
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped
        // Extract user ID from token
        String username = jwtService.getUsernameFromToken(token);
        UUID userId = userRepository.findByEmail(username).getId();
        eventDto.setCreatorId(userId);
        String bucketName = "fredeventsystem";


        String key = UUID.randomUUID()+"_" + userId;

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
    @GetMapping("/get/all")
    public ResponseEntity<APIResponse<Page<EventDto>>> getEventsByCreatorFromToken(@RequestHeader("Authorization") String token,@RequestParam int page,@RequestParam int size) {
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped

        // Extract user ID from token
        String username = jwtService.getUsernameFromToken(token);  // Ensure the method works
        UUID userId = userRepository.findByEmail(username).getId();

        return getEventsByCreator(userId,page,size);  // Call your event fetching method
    }
    @GetMapping("/get/{creator_id}")
    public ResponseEntity<APIResponse<Page<EventDto>>> getEventsByCreator(
            @PathVariable UUID creator_id,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size
    ) {
        Optional<User> optionalUser = userRepository.findById(creator_id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(false, "Error Fetching Events: User with ID: " + creator_id + " does not exist.", null)
            );
        }

        // Fetch paginated events directly
        Optional<Page<Event>> optionalEvents = eventService.getEventByCreatorId(creator_id, page, size);
        if (optionalEvents.isEmpty() || optionalEvents.get().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false, "Error fetching Events: User with ID: " + creator_id + " does not have any events created.", null)
            );
        }

        Page<EventDto> eventDtos = getEventDtos(optionalEvents);

        return ResponseEntity.ok(
                new APIResponse<>(true, "Events fetched successfully", eventDtos)
        );
    }

    private Page<EventDto> getEventDtos(Optional<Page<Event>> optionalEvents) {
        Page<Event> events = optionalEvents.get();

        // Map Event -> EventDto while preserving pagination
        Page<EventDto> eventDtos = events.map(event -> new EventDto(
                event.getId(),
                event.getEventName(),
                event.getEventStartDate(),
                event.getEventEndDate(),
                event.getEventVenue(),
                event.getEventCapacity(),
                event.getCreator().getId(),
                event.getPosterUrl(),
                ticketTypeService.findAllTicketTypesByEvent(event).stream().map(ticketType -> new TicketTypeDTO(
                        ticketType.getId(),
                        ticketType.getTypeCategory(),
                        ticketType.getNumberOfTickets(),
                        ticketType.getPrice(),
                        ticketType.getEvent().getId()
                )).toList(),
                event.getEventTypes()
        ));
        return eventDtos;
    }


    @GetMapping("/send-email")
    public String sendEmail(@RequestParam
                            String email) {
        emailService.sendEmail(
                email,
                "Test Email from Spring Boot",
                "Hello! This is a test email from Spring Boot."
        );
        return "Email Sent!";
    }
    @GetMapping("/get/")//Insecure change
    public ResponseEntity<APIResponse<Page<Event>>> getAllEvents(
            @RequestParam() int page,
            @RequestParam int size
    ) {
        Optional<Page<Event>> optionalEvents = eventService.getAllEvents(page,size);
        if (optionalEvents.isPresent()) {
            APIResponse<Page<Event>> response = new APIResponse<>(
                    true,
                    "Events Fetched Successfully",
                    optionalEvents.get()
            );
            return ResponseEntity.ok(response);
        } else {
            APIResponse<Page<Event>> response = new APIResponse<>(
                    false,
                    "No Events Found",
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @GetMapping("/get/event/{id}")
    public ResponseEntity<APIResponse<Event>> getEventByID(@PathVariable UUID id){
        Optional<Event> event = eventService.getEventById(id);
        if (event.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"Event Not Found",null)
            );
        }
        return ResponseEntity.ok(
                new APIResponse<>(true,"Event fetched succesfully",event.get())
        );

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse<Event>> deleteEvent(@RequestHeader("Authorization") String token,@PathVariable UUID id){
        Optional<Event> optionalEvent = eventService.getEventById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"Event does not exist",null)
            );
        }
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped
        // Extract user ID from token
        String username = jwtService.getUsernameFromToken(token);
        if(!optionalEvent.get().getCreator().getEmail().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new APIResponse<>(false,"You are unauthorized to perform this action!",null)
                    );
        }
        eventService.deleteEventById(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new APIResponse<>(true,"Event Deleted successfully",null)
        );
    }
    @PatchMapping("/update/{eventId}")
    public ResponseEntity<APIResponse<Event>> updateEvent(@PathVariable UUID eventId, @RequestHeader("Authorization") String token,@RequestBody EventDto eventDto){
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        if (eventOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(false,"Event Does not Exist",null));
        }
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped
        // Extract user ID from token
        String username = jwtService.getUsernameFromToken(token);
        if(!eventOptional.get().getCreator().getEmail().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new APIResponse<>(false,"You are unauthorized to perform this action!",null)
                    );
        }
        Optional<Event> updatedEvent= eventService.updateEventInfo(eventDto,eventId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new APIResponse<>(true,"Event Info Updated successfully",updatedEvent.get()));
                 }

    @GetMapping("/event-types")
    public ResponseEntity<APIResponse<List<EventType>>>  getAllEventTypes(){
        List<EventType> eventTypes = eventService.getAllEventTypes();
        if(eventTypes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"No Events Found",null)
            );
        }
        return ResponseEntity.ok(
                new APIResponse<>(true,"Events Fetched Successfully",eventTypes)
        );
    }
}
