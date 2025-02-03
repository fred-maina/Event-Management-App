package com.fredmaina.event_management.EventCreationService.repositories;

import com.fredmaina.event_management.EventCreationService.Models.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    Optional<EventType> findByName(String name);
    
}
