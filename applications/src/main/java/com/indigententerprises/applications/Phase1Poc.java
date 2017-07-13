package com.indigententerprises.applications;

import com.indigententerprises.components.FileInvestigativeServiceImplementation;
import com.indigententerprises.components.TrivialStreamTransferService;
import com.indigententerprises.services.FileInvestigativeService;
import com.indigententerprises.services.StreamTransferService;

import com.indigententerprises.domain.FileData;
import com.indigententerprises.domain.FileMetaData;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;

//import com.amazonaws.services.s3.AmazonS3; // i like interfaces but i don't like this one
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

public class Phase1Poc {

    public static void main(final String [] args) {

        final FileInvestigativeService fileInvestigativeService = new FileInvestigativeServiceImplementation();
        final StreamTransferService streamTransferService = new TrivialStreamTransferService();

        if (args.length != 3) {
            throw new RuntimeException("usage: <exec> " + Phase1Poc.class.getName() + " <AWS bucket-name> <directory> <file>");
        } else {
            final String directoryName = args[1];
            final String fileName = args[2];
            final File directory = new File(directoryName);
            final File file = new File(directory, fileName);

            if (!file.canRead()) {
                throw new RuntimeException("file " + file.getName() + " is not accessible");
            } else {
                final String requestedBucketName = args[0];

                try {
                    final EnvironmentVariableCredentialsProvider credentialsProvider =
                            new EnvironmentVariableCredentialsProvider();
                    final AmazonS3Client s3Client =
                            new AmazonS3Client(credentialsProvider);

                    final AWSCredentials credentials =
                            credentialsProvider.getCredentials();
                    System.out.println("credentials: (" + credentials.getAWSAccessKeyId() + ", " + credentials.getAWSSecretKey() + ")");

                    if (! s3Client.doesBucketExist(requestedBucketName)) {
                        throw new RuntimeException("bucket " + requestedBucketName + " not found");
                    } else {
                        final String targetBucketName = requestedBucketName;
                        final FileData fileData = fileInvestigativeService.investigate(file);
                        final FileMetaData fileMetaData = fileData.getFileMetaData();

                        // begin write to S3
                        final ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(fileMetaData.getSize());

                        final PutObjectRequest request =
                                new PutObjectRequest(targetBucketName, fileMetaData.getName(), fileData.getInputStream(), metadata);

                        s3Client.putObject(request);
                        // end

                        // begin read from S3
                        final GetObjectRequest getObjectRequest = new GetObjectRequest(targetBucketName, fileMetaData.getName());
                        S3Object s3Object = s3Client.getObject(getObjectRequest);
                        // end

                        try {
                            final File targetFile = new File(directory, "target.png");
                            final FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

                            try {
                                final S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

                                try {
                                    streamTransferService.transferStreamData(s3ObjectInputStream, fileOutputStream);
                                } finally {
                                    s3ObjectInputStream.close();
                                }
                            } finally {
                                fileOutputStream.close();
                            }
                        } finally {
                            s3Object.close();
                        }
                    }

                    s3Client.shutdown();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchElementException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
