package com.indigententerprises.components.files;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 *
 * @author jonniesavel
 */
public class FileAttributesTest {

    @Test
    public void test() throws Exception {

        final Path path = Paths.get(System.getProperty("user.home"));
        final Path realPath = path.toRealPath();
        System.out.println(Files.getAttribute(realPath, "basic:creationTime"));

        //System.out.println(Files.getFileStore(Paths.get(" ./ ")).supportsFileAttributeView(UserDefinedFileAttributeView.class));
        System.out.println(Files.getFileStore(Paths.get(System.getProperty("user.home"))).supportsFileAttributeView(UserDefinedFileAttributeView.class));
    }
}
