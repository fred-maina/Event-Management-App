package com.fredmaina.event_management.EventCreationService.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name="event_id" ,referencedColumnName = "event_id")
    private Event event;

    @Override
    public String toString() {
        return "TicketType{" +
                "ticketType='" + typeCategory + '\'' +
                '}';
    }

}
