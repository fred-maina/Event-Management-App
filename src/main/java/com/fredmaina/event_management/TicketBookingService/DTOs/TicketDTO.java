package com.fredmaina.event_management.TicketBookingService.DTOs;

import com.fredmaina.event_management.AuthService.models.User;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TicketDTO  {
    private UUID ticketTypeId;
    private UUID eventId;
}
