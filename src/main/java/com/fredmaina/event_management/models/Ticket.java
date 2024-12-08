package com.fredmaina.event_management.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Ticket {

    public enum PaymentStatus {
        APPROVED,
        PENDING
    }

    public enum TicketStatus {
        UNUSED,
        USED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "purchased_on")
    private LocalDateTime purchasedOn = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketStatus ticketStatus = TicketStatus.UNUSED;

    @Column(name = "ticket_code", nullable = false, unique = true)
    private UUID ticketCode = UUID.randomUUID();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_type", referencedColumnName = "type_id")
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
