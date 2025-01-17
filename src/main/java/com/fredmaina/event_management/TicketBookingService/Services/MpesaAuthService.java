package com.fredmaina.event_management.TicketBookingService.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fredmaina.event_management.TicketBookingService.DTOs.MpesaAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class MpesaAuthService {

    @Autowired
    private RestTemplate restTemplate;

    private String accessToken;
    private Instant expiryTime;

    public synchronized String getAuthToken() throws JsonProcessingException {
        // Check if the token is valid or expired
        if (accessToken == null || Instant.now().isAfter(expiryTime)) {
            fetchAuthToken();
        }
        return accessToken;
    }

    private void fetchAuthToken() throws JsonProcessingException {
        // Safaricom OAuth URL
        String url = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";

        // Create request entity with headers
        HttpEntity<String> entity = getStringHttpEntity();

        // Make the GET request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the response and update the token and expiry time
            ObjectMapper objectMapper = new ObjectMapper();
            MpesaAuthResponse authResponse = objectMapper.readValue(response.getBody(), MpesaAuthResponse.class);
            this.accessToken = authResponse.getAccessToken();
            // Calculate expiry time (current time + expires_in seconds)
            this.expiryTime = Instant.now().plusSeconds(Long.parseLong(authResponse.getExpiresIn()));
        } else {
            throw new RuntimeException("Failed to fetch M-Pesa Auth Token: " + response.getStatusCode());
        }
    }

    public static HttpEntity<String> getStringHttpEntity() {

        String authHeaderValue = "Basic OFN1QmRjajBld2hPdXFCa2lVam55NTB3a2hzWjhZZ1I0N0l1cDZRU2xiVDJCdEdnOlN6QWo3NEttSjdDWk1SOHBKRVdINU5xZDFXVHg0c2hZZFRQbXk2NTdXUkNpc3BQNk5nY1VBWFVHN3pMdG1vY3g=";

        // Set headers (Authorization)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeaderValue);
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(headers);
    }
}
