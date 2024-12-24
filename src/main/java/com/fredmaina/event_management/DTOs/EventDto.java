package com.fredmaina.event_management.DTOs;

import com.fredmaina.event_management.models.TicketType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class EventDto {

    private UUID id;
    private  String eventName;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String eventVenue;
    private int eventCapacity=-1;
    private UUID creatorId;
    private String posterUrl;
    private List<TicketTypeDTO> ticketType;

}
