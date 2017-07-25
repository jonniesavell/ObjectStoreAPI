package com.indigententerprises.applications;

import com.indigententerprises.components.FileInvestigativeServiceImplementation;
import com.indigententerprises.components.MetaDataServiceImplementation;
import com.indigententerprises.components.ObjectService;
import com.indigententerprises.components.ObjectServiceImplementation;
import com.indigententerprises.components.TrivialStreamTransferService;

import com.indigententerprises.services.FileInvestigativeService;
import com.indigententerprises.services.StreamTransferService;
import com.indigententerprises.services.SystemException;

import com.indigententerprises.domain.FileData;
import com.indigententerprises.domain.Handle;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Phase2Poc {

    public static void main(final String [] args) throws SystemException {

        if (args.length != 3) {
            throw new RuntimeException("usage: <exec> " + Phase2Poc.class.getName() + " <AWS bucket-name> <directory> <file>");
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
                    final AmazonS3Client s3Client = (AmazonS3Client)
                            AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();
                    final AWSCredentials credentials =
                            credentialsProvider.getCredentials();

                    System.out.println("credentials: (" + credentials.getAWSAccessKeyId() + ", " + credentials.getAWSSecretKey() + ")");

                    final boolean bucketExists =
                            s3Client.doesBucketExist(requestedBucketName);
                    s3Client.shutdown();

                    if (!bucketExists) {
                        throw new RuntimeException("bucket " + requestedBucketName + " not found");
                    } else {
                        final String targetBucketName = requestedBucketName;
                        final FileInvestigativeService fileInvestigativeService =
                                new FileInvestigativeServiceImplementation();
                        final FileData fileData = fileInvestigativeService.investigate(file);
                        final StreamTransferService streamTransferService = new TrivialStreamTransferService();
                        final ObjectServiceImplementation primitiveObjectService =
                                new ObjectServiceImplementation(
                                        credentialsProvider,
                                        targetBucketName,
                                        streamTransferService
                                );
                        final MetaDataServiceImplementation primitiveMetaDataService =
                                new MetaDataServiceImplementation();
                        final ObjectService objectService =
                                new ObjectService(primitiveObjectService, primitiveMetaDataService);
                        final Handle handle;

                        // begin write to S3
                        handle = objectService.storeObjectAndMetaData(
                                fileData.getInputStream(),
                                (int) fileData.getFileMetaData().getSize(),
                                new HashMap<>());
                        // end

                        final FileOutputStream fileOutputStream = new FileOutputStream(file, false);

                        // truncate the local file
                        try {
                            FileChannel outChannel = fileOutputStream.getChannel();

                            try {
                                outChannel.truncate(0);
                            } finally {
                                outChannel.close();
                            }
                        } finally {
                            fileOutputStream.close();
                        }

                        final FileOutputStream fileOutputStream2 = new FileOutputStream(file);

                        try {
                            // begin read from S3
                            Map<String, Object> metadata =
                                    objectService.retrieveObjectAndMetaData(handle, fileOutputStream2);
                            // end

                            System.out.println(metadata);
                        } finally {
                            fileOutputStream2.close();
                        }
                    }
                } catch(IOException e){
                    throw new SystemException("", e);
                } catch(NoSuchElementException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
