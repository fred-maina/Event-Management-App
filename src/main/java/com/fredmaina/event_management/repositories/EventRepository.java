package com.fredmaina.event_management.repositories;

import com.fredmaina.event_management.models.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    public List<Event> findByCreatorId(UUID id);

}
