package com.fredmaina.event_management.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="Events")
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_id")
    private Integer id;

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

    @JoinColumn(name="creator_id",referencedColumnName = "user_id")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;


}
