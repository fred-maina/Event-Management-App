package com.fredmaina.event_management.TicketBookingService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponseDTO {

    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;

    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseDescription")
    private String responseDescription;

    @JsonProperty("CustomerMessage")
    private String customerMessage;
}
