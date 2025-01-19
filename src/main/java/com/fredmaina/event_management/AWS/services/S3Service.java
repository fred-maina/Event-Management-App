package com.fredmaina.event_management.AWS.services;

import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class S3Service {

    @Autowired
    private  S3Client s3Client;


    public void uploadFileToS3(String bucketName, MultipartFile multipartFile, String key) {
        try {
            // Convert MultipartFile to Path
            Path tempFilePath = convertMultipartFileToPath(multipartFile);

            // Upload file to S3
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build(), tempFilePath);

            // Delete the temporary file after upload
            Files.delete(tempFilePath);

        } catch (IOException e) {
            throw new RuntimeException("Error while converting MultipartFile to File", e);
        } catch (S3Exception e) {
            throw new RuntimeException("Error while uploading file to S3", e);
        }
    }

    private Path convertMultipartFileToPath(MultipartFile multipartFile) throws IOException {
        // Create a temporary file
        Path tempFilePath = Files.createTempFile("temp", multipartFile.getOriginalFilename());

        // Write the contents of the MultipartFile to the temporary file
        Files.write(tempFilePath, multipartFile.getBytes());

        return tempFilePath;
    }
    public void deleteFileFromKey(String bucketName, String key){
        s3Client.deleteObject(DeleteObjectRequest.builder().key(key).bucket(bucketName).build());

    }
    public void deleteFileFromURL(String bucketName,String url) throws MalformedURLException {

        String key= new URL(url).getPath().substring(1);
        deleteFileFromKey(bucketName,key);
    }



    public String getFileUrl(String bucketName, String key) {
        return s3Client.utilities().getUrl(builder -> builder
                        .bucket(bucketName)
                        .key(key))
                .toString();
    }
}
