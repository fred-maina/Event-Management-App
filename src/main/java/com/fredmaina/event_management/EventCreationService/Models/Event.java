package com.fredmaina.event_management.EventCreationService.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fredmaina.event_management.AuthService.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name="Events")
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="event_id")
    private UUID id;


    @Column(name="event_name",nullable = false,length = 255)
    private  String eventName;

    @Column(name="event_start_date",nullable = false)
    private LocalDateTime eventStartDate;

    @Column(name="event_end_date")
    private LocalDateTime eventEndDate;

    @Column(name="event_venue",nullable = false,length = 255)
    private String eventVenue;

    @Column(name="event_capacity",nullable = false)
    private int eventCapacity=-1;//capacity -1 means the space is unlimited

    private String posterUrl;

    @JoinColumn(name="creator_id",referencedColumnName = "user_id")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private User creator;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TicketType> ticketTypes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "event_event_type",  // Name of the join table
            joinColumns = @JoinColumn(name = "event_id"),  // Foreign key to Event
            inverseJoinColumns = @JoinColumn(name = "event_type_id")  // Foreign key to EventType
    )
    private Set<EventType> eventTypes = new HashSet<>();

    public Event(UUID id, String eventName, LocalDateTime eventStartDate, LocalDateTime eventEndDate, String eventVenue, int eventCapacity, String posterUrl, User creator,Set<EventType> eventTypes) {
        this.id = id;
        this.eventName = eventName;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventVenue = eventVenue;
        this.eventCapacity = eventCapacity;
        this.posterUrl = posterUrl;
        this.creator = creator;
        this.eventTypes=eventTypes;

    }
    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", eventDate='" + eventStartDate + '\'' +
                '}';
    }

}
