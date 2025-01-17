package com.fredmaina.event_management.EventCreationService.DTOs;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class EventDto  {

    private UUID id;
    private  String eventName;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String eventVenue;
    private Integer eventCapacity;
    private UUID creatorId;
    private String posterUrl;
    private List<TicketTypeDTO> ticketType;

}
