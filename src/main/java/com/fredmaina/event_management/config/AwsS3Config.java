package com.fredmaina.event_management.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Bean
    public AmazonS3 amazonS3() {
        // Hardcoded AWS credentials and region
        String awsRegion = System.getenv("AWS_REGION");                     // Replace with your desired region
        String awsAccessKey =System.getenv("AWS_ACCESS_KEY_ID") ;     // Replace with your access key
        String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");  // Replace with your secret key


        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
