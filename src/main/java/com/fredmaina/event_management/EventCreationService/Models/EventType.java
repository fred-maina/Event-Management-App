package com.fredmaina.event_management.EventCreationService.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;

    @ManyToMany(mappedBy = "eventTypes")
    @JsonIgnore
    private Set<Event> events = new HashSet<>();
}
