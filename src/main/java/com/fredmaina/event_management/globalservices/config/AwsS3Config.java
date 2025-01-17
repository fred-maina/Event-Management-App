package com.fredmaina.event_management.globalservices.config;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {



    @Bean
    public S3Client s3Client() {
        // Retrieve AWS credentials and region from environment variables
        String awsRegion = System.getenv("AWS_REGION");                     // Replace with your desired region
        String awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID");          // Replace with your access key
        String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");      // Replace with your secret key

        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(
                software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
        );

        return S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.of(awsRegion))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}
