package com.fredmaina.event_management.repositories;

import com.fredmaina.event_management.models.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    public List<Event> findByCreatorId(int id);

}
