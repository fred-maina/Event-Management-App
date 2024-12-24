package com.fredmaina.event_management.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void uploadFileToS3(String bucketName, MultipartFile multipartFile, String key) {
        try {
            // Convert MultipartFile to File
            File file = convertMultipartFileToFile(multipartFile);

            // Upload file to S3
            amazonS3.putObject(new PutObjectRequest(bucketName, key, file));

            // Delete the temporary file after upload
            file.delete();

        } catch (IOException e) {
            throw new RuntimeException("Error while converting MultipartFile to File", e);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // Create a temporary file
        File tempFile = File.createTempFile("temp", multipartFile.getOriginalFilename());

        // Write the contents of the MultipartFile to the temporary file
        multipartFile.transferTo(tempFile);

        return tempFile;
    }

    public String getFileUrl(String bucketName, String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }
}
