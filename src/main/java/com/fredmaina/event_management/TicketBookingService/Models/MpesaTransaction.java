package com.fredmaina.event_management.TicketBookingService.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class MpesaTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="transaction_id")
    private UUID id;

    private String merchantRequestId;
    private String checkoutRequestId;
    private String transactionCode;
    private boolean successful;
    private LocalDateTime transactionDate;
    private int amount;
    private  String ticketCode;

}
