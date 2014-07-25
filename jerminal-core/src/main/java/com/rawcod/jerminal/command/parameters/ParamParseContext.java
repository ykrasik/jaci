package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:24
 */
public class ParamParseContext {
    private final FileSystemManager fileSystemManager;
    private final ShellDirectory currentDirectory;

    public ParamParseContext(FileSystemManager fileSystemManager, ShellDirectory currentDirectory) {
        this.fileSystemManager = fileSystemManager;
        this.currentDirectory = currentDirectory;
    }

    public FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("fileSystemManager", fileSystemManager)
            .add("currentDirectory", currentDirectory)
            .toString();
    }
}
