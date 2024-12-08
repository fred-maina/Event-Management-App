package com.fredmaina.event_management.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@ToString
public class EventDto {

    private Integer id;
    private  String eventName;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String eventVenue;
    private int eventCapacity=-1;
    private int creatorId;

}
