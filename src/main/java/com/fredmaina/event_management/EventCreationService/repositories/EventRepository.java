package com.fredmaina.event_management.EventCreationService.repositories;

import com.fredmaina.event_management.EventCreationService.Models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    public List<Event> findByCreatorId(UUID id);

}
