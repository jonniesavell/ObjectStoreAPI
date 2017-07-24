package com.indigententerprises.components.objects;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;

import com.indigententerprises.components.ObjectServiceImplementation;
import com.indigententerprises.components.TrivialStreamTransferService;
import com.indigententerprises.services.StreamTransferService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 * test the meta-data service
 *
 * @author jonniesavel
 *
 */
public class ObjectManipulationTest {

    private File file;
    private int size;

    @Before
    public void before() throws IOException {

        file = new File("object.bin");

        if (file.exists()) {
            final FileOutputStream fileOutputStream = new FileOutputStream(file, false);

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
        } else {
            // file doesn't exist => no need to do anything
        }

        final FileOutputStream fileOutputStream = new FileOutputStream(file, false);

        try {
            byte byteNumber = Byte.MIN_VALUE;
            this.size = 0;

            while (byteNumber < Byte.MAX_VALUE) {

                fileOutputStream.write(byteNumber);

                // update mutable state
                byteNumber++;
                this.size++;
            }
        } finally {
            fileOutputStream.close();
        }
    }

    @After
    public void afterTest() throws IOException {

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testObjectStuff() throws Exception {

        // this is where YOU enter YOUR credentials.
        final EnvironmentVariableCredentialsProvider credentialsProvider =
                new EnvironmentVariableCredentialsProvider();
        final String targetBucketName = "com.indigententerprises.photos";
        final StreamTransferService streamTransferService = new TrivialStreamTransferService();
        final UUID uuid = UUID.randomUUID();
        final ObjectServiceImplementation systemUnderTest =
                new ObjectServiceImplementation(
                        credentialsProvider,
                        targetBucketName,
                        streamTransferService
                );

        final FileInputStream inputStream = new FileInputStream(this.file);

        try {
            systemUnderTest.persistObject(uuid.toString(), this.size, inputStream);

            final File resultingFile = File.createTempFile("temp", ".tmp");
            resultingFile.deleteOnExit();

            final FileOutputStream outputStream = new FileOutputStream(resultingFile);

            try {
                systemUnderTest.retrieveObject(uuid.toString(), outputStream);
            } finally {
                outputStream.close();
            }

            byte [] originalContents  = Files.readAllBytes(this.file.toPath());
            byte [] resultingContents = Files.readAllBytes(resultingFile.toPath());

            Assert.assertTrue(Arrays.equals(originalContents, resultingContents));
        } finally {
            inputStream.close();
        }
    }
}
