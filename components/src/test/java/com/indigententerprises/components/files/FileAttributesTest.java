package com.indigententerprises.components.files;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 *
 * test for the ability to use extended file attributes to store
 * object attributes while the object and attributes are transferred
 * to the client. can't leave an object input stream (a resource owned
 * by a third-party service) open, right?
 *
 * @author jonniesavel
 *
 */
public class FileAttributesTest {

    @Test
    public void test() throws Exception {

        final File tmpFile = File.createTempFile("temp", ".tmp");
        tmpFile.deleteOnExit();

        System.out.println("path: " + tmpFile.getAbsolutePath());

        final Path path = Paths.get(tmpFile.getAbsolutePath());
        FileStore store = Files.getFileStore(path);

        // Assert.assertTrue(store.supportsFileAttributeView(UserDefinedFileAttributeView.class));

        // conclusion: would have preferred to store object attributes within
        //             extended file attributes but, sadly, this is not possible.
        //             will need to store object attributes elsewhere, possibly
        //             including within another file. deep shame.
    }
}
