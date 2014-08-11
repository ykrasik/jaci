package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 09/08/2014
 * Time: 20:51
 */
public class CurrentDirectoryContainer {
    private ShellDirectory currentDirectory;

    public CurrentDirectoryContainer() {
    }

    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(ShellDirectory currentDirectory) {
        final ShellDirectory prevDirectory = this.currentDirectory;
        this.currentDirectory = checkNotNull(currentDirectory, "currentDirectory");
        if (currentDirectory != prevDirectory) {
            // TODO: Call a listener?
        }
    }
}
