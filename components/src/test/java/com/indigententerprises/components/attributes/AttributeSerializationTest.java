package com.indigententerprises.components.attributes;

import com.indigententerprises.components.MetaDataServiceImplementation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * test the meta-data service
 *
 * @author jonniesavel
 *
 */
public class AttributeSerializationTest {

    private File file;

    @Before
    public void before() throws IOException {

        file = new File("metadata.avro");

        if (file.exists()) {
            FileChannel outChan = new FileOutputStream(file, false).getChannel();
            outChan.truncate(0);
            outChan.close();
        } else {
            // file doesn't exist => no need to do anything
        }
    }

    @After
    public void afterTest() throws IOException {

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testPopulateAttributes() throws Exception {

        final MetaDataServiceImplementation systemUnderTest =
                new MetaDataServiceImplementation();
        final HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("pants", 5);
        attributes.put("socks", 3);

        final FileOutputStream outputStream = new FileOutputStream(file);

        try {
            systemUnderTest.serializeMetaData(outputStream, attributes);
        } finally {
            outputStream.close();
        }

        final FileInputStream inputStream =
                new FileInputStream(file);
        final Map<String, Object> result =
                systemUnderTest.deserializeMetaData(inputStream);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void testPopulateNoAttributes() throws Exception {

        final MetaDataServiceImplementation systemUnderTest =
                new MetaDataServiceImplementation();
        final Map<String, Object> attributes = Collections.emptyMap();
        final FileOutputStream outputStream = new FileOutputStream(file);

        try {
            systemUnderTest.serializeMetaData(outputStream, attributes);
        } finally {
            outputStream.close();
        }

        final FileInputStream inputStream =
                new FileInputStream(file);
        final Map<String, Object> result =
                systemUnderTest.deserializeMetaData(inputStream);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 0);
    }
}
