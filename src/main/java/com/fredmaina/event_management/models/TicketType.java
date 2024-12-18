package com.fredmaina.event_management.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Reference;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "TicketType")
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    int id;

    @Column(name="type_category")
    String typeCategory;//e.g vip VVIP Regular EarlyBird

    @Column(name="price")
    int price;

    @Column(name="number_of_tickets")
    int numberOfTickets;

    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name="event_id" ,referencedColumnName = "event_id")
    private Event event;

}
