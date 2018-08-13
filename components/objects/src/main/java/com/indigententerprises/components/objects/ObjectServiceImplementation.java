package com.indigententerprises.components.objects;

import com.indigententerprises.services.objects.ObjectService;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonClientException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import com.indigententerprises.services.common.SystemException;
import com.indigententerprises.services.streams.StreamTransferService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

public class ObjectServiceImplementation implements ObjectService {

    private final AWSCredentialsProvider credentialsProvider;
    private final String targetBucketName;
    private final StreamTransferService streamTransferService;


    public ObjectServiceImplementation(
            final AWSCredentialsProvider credentialsProvider,
            final String targetBucketName,
            final StreamTransferService streamTransferService) throws SystemException {

        this.credentialsProvider = credentialsProvider;
        this.targetBucketName = targetBucketName;
        this.streamTransferService = streamTransferService;

        try {
            // down-casting is BAAAD
            final AmazonS3Client s3Client = (AmazonS3Client)
                    AmazonS3ClientBuilder.standard().withCredentials(this.credentialsProvider).build();

            try {
                if (!s3Client.doesBucketExist(this.targetBucketName)) {
                    throw new RuntimeException("bucket " + this.targetBucketName + " not found");
                } else {
                    final AWSCredentials credentials = credentialsProvider.getCredentials();
                }
            } finally {
                s3Client.shutdown();
            }
        } catch (AmazonServiceException e) {
            throw new SystemException("", e);
        } catch (AmazonClientException e) {
            throw new SystemException("", e);
        }
    }

    @Override
    public synchronized void persistObject(
            final String id,
            final int size,
            final InputStream sourceInputStream) throws SystemException {

        try {
            // down-casting is BAAAD
            final AmazonS3Client s3Client = (AmazonS3Client)
                    AmazonS3ClientBuilder.standard().withCredentials(this.credentialsProvider).build();

            try {
                final ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(size);

                final PutObjectRequest request =
                        new PutObjectRequest(targetBucketName, id, sourceInputStream, metadata);

                s3Client.putObject(request);
            } finally {
                s3Client.shutdown();
            }
        } catch (AmazonServiceException e) {
            throw new SystemException("", e);
        } catch (AmazonClientException e) {
            throw new SystemException("", e);
        }
    }

    @Override
    public synchronized void retrieveObject(
            final String id,
            final OutputStream outputStream) throws NoSuchElementException, SystemException {

        try {
            // down-casting is BAAAD
            final AmazonS3Client s3Client = (AmazonS3Client)
                    AmazonS3ClientBuilder.standard().withCredentials(this.credentialsProvider).build();

            try {
                if (! s3Client.doesObjectExist(this.targetBucketName, id)) {
                    throw new NoSuchElementException();
                } else {
                    final GetObjectRequest getObjectRequest =
                            new GetObjectRequest(this.targetBucketName, id);
                    final S3Object s3Object =
                            s3Client.getObject(getObjectRequest);

                    try {
                        final S3ObjectInputStream s3ObjectInputStream =
                                s3Object.getObjectContent();

                        try {
                            streamTransferService.transferStreamData(s3ObjectInputStream, outputStream);
                        } finally {
                            s3ObjectInputStream.close();
                        }
                    } finally {
                        s3Object.close();
                    }
                }
            } finally {
                s3Client.shutdown();
            }
        } catch (IOException e) {
            throw new SystemException("", e);
        } catch (AmazonServiceException e) {
            throw new SystemException("", e);
        } catch (AmazonClientException e) {
            throw new SystemException("", e);
        }
    }
}
