package com.indigententerprises.components;

import com.indigententerprises.domain.Handle;
import com.indigententerprises.services.SystemException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author jonniesavell
 */
public class ObjectService {

    private final String SUFFIX = "__METADATA";
    private final ObjectServiceImplementation primitiveObjectService;
    private final MetaDataServiceImplementation primitiveMetaDataService;

    public ObjectService(
            final ObjectServiceImplementation primitiveObjectService,
            final MetaDataServiceImplementation primitiveMetaDataService
    ) {
        this.primitiveObjectService = primitiveObjectService;
        this.primitiveMetaDataService = primitiveMetaDataService;
    }

    public Map<String, Object> retrieveObjectMetaData(final Handle handle)
            throws NoSuchElementException, SystemException {

        try {
            final File file = File.createTempFile("temp", ".tmp");

            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(file);

                try {
                    final String identifier = handle.identifier + SUFFIX;
                    this.primitiveObjectService.retrieveObject(identifier, fileOutputStream);
                } finally {
                    fileOutputStream.close();

                    final FileInputStream fileInputStream = new FileInputStream(file);

                    try {
                        return this.primitiveMetaDataService.deserializeMetaData(fileInputStream);
                    } finally {
                        fileInputStream.close();
                    }
                }
            } finally {
                if (! file.delete()) {
                    file.deleteOnExit();
                }
            }
        } catch (IOException e) {
            throw new SystemException("", e);
        }
    }

    public void retrieveObject(
            final Handle handle,
            final InputStream inputStream)
            throws NoSuchElementException, SystemException {
    }
}
