package com.indigententerprises.services;

import com.indigententerprises.domain.FileData;
import com.indigententerprises.domain.FileMetaData;
import com.indigententerprises.domain.Handle;

import java.util.NoSuchElementException;

/**
 *
 * @author jonniesavell
 */
public interface FileRetrievalService {

    public FileMetaData retrieveFileMetaData(final Handle handle)
            throws NoSuchElementException, SystemException;

    public FileData retrieveFile(final Handle handle)
            throws NoSuchElementException, SystemException;
}
