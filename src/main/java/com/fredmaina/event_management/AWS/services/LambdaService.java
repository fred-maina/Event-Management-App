package com.fredmaina.event_management.AWS.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.core.SdkBytes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class LambdaService {
    @Autowired
    LambdaClient lambdaClient;

    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson's ObjectMapper for JSON conversion

    public String invokeLambda() {
        // Create a Map for payload data
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("event_name", "Sample Event");
        payloadMap.put("event_date", "2025-01-01");
        payloadMap.put("buyer_name", "John Doe");
        payloadMap.put("ticket_code", "12345");
        payloadMap.put("payment_mode", "Credit Card");
        payloadMap.put("ticket_type", "VIP");
        payloadMap.put("ticket_price", "100");
        payloadMap.put("purchase_time", "2025-01-01T10:00:00Z");

        // Convert the Map to a JSON string
        String jsonString = convertMapToJsonString(payloadMap);

        // Convert the string to byte array and then to SdkBytes
        SdkBytes sdkBytes = SdkBytes.fromByteArray(jsonString.getBytes(StandardCharsets.UTF_8));

        // Invoke Lambda
        String response = callLambdaFunction(sdkBytes);

        return response;
    }

    private String convertMapToJsonString(Map<String, Object> map) {
        try {
            // Convert Map to JSON string using Jackson's ObjectMapper
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";  // Return empty JSON in case of an error
        }
    }

    private String callLambdaFunction(SdkBytes payload) {
        // Build the invoke request
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName("QRCodeGenerator")  // Replace with your Lambda function name
                .payload(payload)
                .build();

        // Call Lambda (you need to use the LambdaClient here)
        try (LambdaClient lambdaClient = LambdaClient.create()) {
            InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
            // Get the response as string
            return invokeResponse.payload().asString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error invoking Lambda function";
        }
    }
}
