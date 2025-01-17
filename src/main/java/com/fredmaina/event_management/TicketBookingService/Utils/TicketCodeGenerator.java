package com.fredmaina.event_management.TicketBookingService.Utils;

import java.security.SecureRandom;
import java.time.Instant;

public class TicketCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_PART_LENGTH = 6; // Length of the random part
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a unique ticket code with a Luhn checksum.
     *
     * @param eventName The name of the event.
     * @return A Luhn-compliant ticket code.
     */
    public static String generateTicketCode(String eventName) {
        String sanitizedEventName = sanitizeEventName(eventName);
        String timestampPart = Long.toString(Instant.now().getEpochSecond(), 36).toUpperCase();
        StringBuilder randomPart = new StringBuilder(RANDOM_PART_LENGTH);

        for (int i = 0; i < RANDOM_PART_LENGTH; i++) {
            randomPart.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        // Combine event name, timestamp, and random part
        String baseCode = sanitizedEventName + timestampPart + randomPart;

        // Add Luhn checksum
        return luhnGenerate(baseCode);
    }

    /**
     * Sanitizes the event name by keeping only alphanumeric characters and converting to uppercase.
     * Limits the sanitized name to a maximum of 5 characters.
     *
     * @param eventName The original event name.
     * @return A sanitized event name.
     */
    private static String sanitizeEventName(String eventName) {
        return eventName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "")
                .substring(0, Math.min(5, eventName.length()));
    }

    /**
     * Appends a Luhn checksum to the input code.
     *
     * @param baseCode The base code to which the Luhn checksum will be added.
     * @return The code with the appended Luhn checksum.
     */
    private static String luhnGenerate(String baseCode) {
        int sum = 0;
        boolean doubleDigit = true;

        // Process the digits in reverse order
        for (int i = baseCode.length() - 1; i >= 0; i--) {
            char c = baseCode.charAt(i);
            int digit = Character.isDigit(c) ? c - '0' : c - 'A' + 10;

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        // Calculate the checksum digit
        int checksum = (10 - (sum % 10)) % 10;

        // Append the checksum to the original code
        return baseCode + checksum;
    }
}
