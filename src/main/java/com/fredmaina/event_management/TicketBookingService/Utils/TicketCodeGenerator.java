package com.fredmaina.event_management.TicketBookingService.Utils;

import java.security.SecureRandom;
import java.time.Instant;

public class TicketCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_PART_LENGTH = 6; // Length of the random part
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateTicketCode(String eventName) {
        // Sanitize and take the first part of the event name
        String sanitizedEventName = sanitizeEventName(eventName);

        String timestampPart = Long.toString(Instant.now().getEpochSecond(), 36).toUpperCase();

        StringBuilder randomPart = new StringBuilder(RANDOM_PART_LENGTH);
        for (int i = 0; i < RANDOM_PART_LENGTH; i++) {
            randomPart.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        // Combine event name, timestamp, and random part
        return sanitizedEventName + "-" + timestampPart + "-" + randomPart.toString();
    }

    private static String sanitizeEventName(String eventName) {
        // Convert to uppercase, remove non-alphanumeric characters, and limit to 5 characters
        return eventName.toUpperCase().replaceAll("[^A-Z0-9]", "").substring(0, Math.min(5, eventName.length()));
    }


}
