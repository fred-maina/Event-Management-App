package com.fredmaina.event_management.EventCreationService.Contollers;

import com.fredmaina.event_management.AuthService.utils.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import com.fredmaina.event_management.globalservices.DTOs.APIResponse;
import com.fredmaina.event_management.EventCreationService.DTOs.EventDto;
import com.fredmaina.event_management.EventCreationService.DTOs.TicketTypeDTO;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.AuthService.repositories.UserRepository;
import com.fredmaina.event_management.EventCreationService.services.EventService;
import com.fredmaina.event_management.globalservices.services.S3Service;
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
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping(value="/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<Event>> createEvent(@RequestPart("event") @Valid EventDto eventDto, @RequestPart("poster") MultipartFile poster,@RequestHeader("Authorization") String token){
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped
        // Extract user ID from token
        String username = jwtUtil.getUsernameFromToken(token);  // Ensure the method works
        UUID userId = userRepository.findByEmail(username).getId();
        eventDto.setCreatorId(userId);
         String bucketName = "fredeventsystem";


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
    @GetMapping("/get/all")
    public ResponseEntity<APIResponse<List<EventDto>>> getEventsByCreatorFromToken(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped

        // Extract user ID from token
        String username = jwtUtil.getUsernameFromToken(token);  // Ensure the method works
        UUID userId = userRepository.findByEmail(username).getId();

        return getEventsByCreator(userId);  // Call your event fetching method
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
        List<EventDto> eventDtos = events.stream().map(event -> new EventDto(
                event.getId(),
                event.getEventName(),
                event.getEventStartDate(),
                event.getEventEndDate(),
                event.getEventVenue(),
                event.getEventCapacity(),
                event.getCreator().getId(),
                event.getPosterUrl(),
                ticketTypeService.findAllTicketTypesByEvent(event).stream().map(ticketType-> new TicketTypeDTO(
                        ticketType.getId(),
                        ticketType.getTypeCategory(),
                        ticketType.getNumberOfTickets(),
                        ticketType.getPrice(),
                        ticketType.getEvent().getId())).toList() )).toList();

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
    public ResponseEntity<APIResponse<Event>> deleteEvent(@RequestHeader("Authorization") String token,@PathVariable UUID id){
        Optional<Event> optionalEvent = eventService.getEventById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new APIResponse<>(false,"Event does not exist",null)
            );
        }
        token = token.replace("Bearer ", "").trim();  // Ensure space is also stripped
        // Extract user ID from token
        String username = jwtUtil.getUsernameFromToken(token);
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
        String username = jwtUtil.getUsernameFromToken(token);
        if(!eventOptional.get().getCreator().getEmail().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new APIResponse<>(false,"You are unauthorized to perform this action!",null)
                    );
        }
        Optional<Event> updatedEvent= eventService.updateEventInfo(eventDto,eventId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new APIResponse<>(true,"Event Info Updated successfully",updatedEvent.get())
        );

    }

}
