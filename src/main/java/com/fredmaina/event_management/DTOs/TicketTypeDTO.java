package com.fredmaina.event_management.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeDTO {
    private UUID id;
    private String typeCategory;
    private int numberOfTickets;
    private int price;
    private UUID eventId;
}
