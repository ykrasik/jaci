package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.FileSystemManager;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:24
 */
public class ParseParamContext {
    private final FileSystemManager fileSystemManager;

    public ParseParamContext(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }

    public FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("fileSystemManager", fileSystemManager)
            .toString();
    }
}
