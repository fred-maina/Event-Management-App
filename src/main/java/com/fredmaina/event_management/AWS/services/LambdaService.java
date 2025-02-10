package com.fredmaina.event_management.AWS.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.core.SdkBytes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class LambdaService {
    @Autowired
    LambdaClient lambdaClient;  // Use the autowired LambdaClient

    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson's ObjectMapper for JSON conversion

    public String invokeLambda(Map<String, Object> payloadMap) {
        // Convert the Map to a JSON string
        String jsonString = convertMapToJsonString(payloadMap);

        // Convert the string to byte array and then to SdkBytes
        SdkBytes sdkBytes = SdkBytes.fromByteArray(jsonString.getBytes(StandardCharsets.UTF_8));

        // Invoke Lambda
        return callLambdaFunction(sdkBytes);
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
                .functionName("arn:aws:lambda:eu-north-1:465921435493:function:GenerateUserTicket")  // Your Lambda function ARN
                .payload(payload)
                .build();

        // Call Lambda (use the autowired LambdaClient)
        try {
            InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);  // Use the autowired lambdaClient
            // Get the response as string
            return invokeResponse.payload().asString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error invoking Lambda function";
        }
    }
}
