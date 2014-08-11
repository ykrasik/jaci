package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.ShellFileSystem;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:24
 */
public class ParseParamContext {
    private final ShellFileSystem fileSystem;

    public ParseParamContext(ShellFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public ShellFileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("fileSystemManager", fileSystem)
            .toString();
    }
}
