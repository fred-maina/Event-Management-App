package com.fredmaina.event_management.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Reference;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TicketType")
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    int id;

    @Column(name="type_category")
    String typeCategory;

    @Column(name="price")
    int price;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="event_id" ,referencedColumnName = "event_id")
    private Event event;

}
