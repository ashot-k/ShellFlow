package org.ashot.shellflow.peristence;

import org.ashot.shellflow.exception.CouldNotCreateRequiredFile;

import java.io.File;

public interface FileDataRepository<T> {

    void saveToFile(File file, T data) throws CouldNotCreateRequiredFile;

    T openFromFile(File file) throws CouldNotCreateRequiredFile;
}
