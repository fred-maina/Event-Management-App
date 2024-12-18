package com.fredmaina.event_management.repositories;

import com.fredmaina.event_management.models.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketTypeRepository extends JpaRepository<TicketType,Integer> {
}
