package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:23
 */
public class ShellFileSystem {
    private final ShellDirectory root;

    public ShellFileSystem(ShellDirectory root) {
        this.root = new ShellDirectory("", "Root dir");
    }

    public ShellDirectory getRoot() {
        return root;
    }
}
