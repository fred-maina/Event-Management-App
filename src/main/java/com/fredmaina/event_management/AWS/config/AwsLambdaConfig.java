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
        // Retrieve AWS credentials and region from environment variables
        String awsRegion = System.getenv("AWS_REGION");  // Ensure AWS region is properly set
        String awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID"); // Replace with your access key
        String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY"); // Replace with your secret key

        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(
                software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
        );

        return LambdaClient.builder()
                .region(software.amazon.awssdk.regions.Region.of(awsRegion))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}
