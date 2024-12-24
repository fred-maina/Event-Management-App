package com.fredmaina.event_management.repositories;

import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {
    List<TicketType> findAllByEvent(Event event);
}
