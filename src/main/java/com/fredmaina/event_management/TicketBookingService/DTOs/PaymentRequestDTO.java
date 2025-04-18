package com.fredmaina.event_management.TicketBookingService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Data
@NoArgsConstructor
public class PaymentRequestDTO {

    @JsonProperty("BusinessShortCode")
    private final int businessShortCode = 174379;


    @JsonProperty("Timestamp")
    private  final String timestamp=formatDate();

    @JsonProperty("Password")
    private final String password = generatePassword();


    private String generatePassword() {
        String encodedPassword=businessShortCode+"bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"+this.timestamp;
        return Base64.getEncoder().encodeToString(encodedPassword.getBytes());
    }

    @JsonProperty("TransactionType")
    private final String transactionType = "CustomerPayBillOnline";

    @JsonProperty("Amount")
    private int amount;

    @JsonProperty("PartyA")
    private Long partyA;

    @JsonProperty("PartyB")
    private final int partyB = 174379;

    @JsonProperty("PhoneNumber")
    private Long phoneNumber;

    @JsonProperty("CallBackURL")
    private final String callBackURL ="https://api.events.v0.fredmaina.com/api/ticket/mpesa/callback";

    @JsonProperty("AccountReference")
    private  String accountReference ;

    @JsonProperty("TransactionDesc")
    private String transactionDescription;

   private String formatDate() {
        // Set the timestamp to the current date and time in the required format (UTC)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(ZoneOffset.UTC); // Ensure UTC time zone
        return formatter.format(Instant.now()); // Use Instant for the current UTC time

    }
}
