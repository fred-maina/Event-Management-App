package com.fredmaina.event_management.DTOs;

import com.fredmaina.event_management.models.TicketType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class EventDto {

    private Integer id;
    private  String eventName;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String eventVenue;
    private int eventCapacity=-1;
    private int creatorId;
    private List<TicketTypeDTO> ticketType;

}
