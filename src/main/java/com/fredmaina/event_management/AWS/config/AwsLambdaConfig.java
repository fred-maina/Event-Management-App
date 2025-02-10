package com.fredmaina.event_management.AWS.config;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsLambdaConfig {

    @Bean
    public LambdaClient lambdaClient() {
        // Retrieve AWS region from environment variables
        String awsRegion = "eu-north-1";  // Explicitly setting AWS region

        // Retrieve AWS credentials from environment variables
        String awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

        if (awsAccessKey == null || awsSecretKey == null) {
            throw new IllegalArgumentException("AWS credentials not found in environment variables.");
        }

        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(
                software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
        );

        return LambdaClient.builder()
                .region(software.amazon.awssdk.regions.Region.of(awsRegion))  // Always force the correct region
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}
