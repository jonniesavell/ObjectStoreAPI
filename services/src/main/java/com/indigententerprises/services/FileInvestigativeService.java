package com.indigententerprises.services;

import com.indigententerprises.domain.FileData;

import java.io.File;
import java.util.NoSuchElementException;

public interface FileInvestigativeService {

    public FileData investigate(File file) throws NoSuchElementException;
}
