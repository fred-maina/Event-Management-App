package com.fredmaina.event_management.TicketBookingService.Repositories;

import com.fredmaina.event_management.TicketBookingService.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findTicketByTicketCode(String ticketCode);

    boolean existsByTicketCode(String ticketCode);
}
