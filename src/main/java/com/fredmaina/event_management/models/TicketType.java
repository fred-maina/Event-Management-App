package com.fredmaina.event_management.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Reference;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "TicketType")
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "type_id")
    private UUID id;

    @Column(name="type_category")
    private String typeCategory;//e.g vip VVIP Regular EarlyBird

    @Column(name="price")
    private int price;

    @Column(name="number_of_tickets")
    private int numberOfTickets;

    @ManyToOne(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name="event_id" ,referencedColumnName = "event_id")
    private Event event;

}
