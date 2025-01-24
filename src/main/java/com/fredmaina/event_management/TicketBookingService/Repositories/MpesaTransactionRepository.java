package com.fredmaina.event_management.TicketBookingService.Repositories;

import com.fredmaina.event_management.TicketBookingService.Models.MpesaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MpesaTransactionRepository extends JpaRepository<MpesaTransaction, UUID> {
    List<MpesaTransaction> getMpesaTransactionBySuccessful(boolean successful);
    MpesaTransaction getMpesaTransactionByCheckoutRequestId(String checkoutRequestId);
    MpesaTransaction getMpesaTransactionByTransactionCode(String transactionId);
    MpesaTransaction getMpesaTransactionByTicketCode(String ticketCode);
}
