package com.fredmaina.event_management.EventCreationService.DTOs;

import com.fasterxml.jackson.annotation.*;
import com.fredmaina.event_management.EventCreationService.Models.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class EventDto  {
    public EventDto(UUID id, String eventName, LocalDateTime eventStartDate, LocalDateTime eventEndDate, String eventVenue, Integer eventCapacity, UUID creatorId, String posterUrl, List<TicketTypeDTO> ticketType, Set<EventType> eventTypes) {
        this.id = id;
        this.eventName = eventName;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventVenue = eventVenue;
        this.eventCapacity = eventCapacity;
        this.creatorId = creatorId;
        this.posterUrl = posterUrl;
        this.ticketType = ticketType;
        this.eventTypes = eventTypes;
    }

    private UUID id;
    private  String eventName;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String eventVenue;
    private Integer eventCapacity;
    private UUID creatorId;
    private String posterUrl;
    private List<TicketTypeDTO> ticketType;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Long> eventTypeIds;

    private Set<EventType> eventTypes;
}
