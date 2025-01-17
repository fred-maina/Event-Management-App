package com.fredmaina.event_management.TicketBookingService.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentRequestDTO;
import com.fredmaina.event_management.TicketBookingService.DTOs.PaymentResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Log4j2
@Service
public class MpesaSTKPushService {

    private final MpesaAuthService mpesaAuthService;
    private final RestTemplate restTemplate;
    private final String url = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

    @Autowired
    public MpesaSTKPushService(MpesaAuthService mpesaAuthService, RestTemplate restTemplate) {
        this.mpesaAuthService = mpesaAuthService;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<PaymentResponseDTO> sendSTKPush(String ticketNumber, Long phoneNumber) throws JsonProcessingException {
        // Create the payment request payload
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
        paymentRequestDTO.setPartyA(phoneNumber);
        paymentRequestDTO.setTransactionDescription(ticketNumber);
        paymentRequestDTO.setPhoneNumber(phoneNumber);

        // Add headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + mpesaAuthService.getAuthToken());

        // Wrap the payload and headers in an HttpEntity
        HttpEntity<PaymentRequestDTO> requestEntity = new HttpEntity<>(paymentRequestDTO, headers);


        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, PaymentResponseDTO.class);
    }
}
