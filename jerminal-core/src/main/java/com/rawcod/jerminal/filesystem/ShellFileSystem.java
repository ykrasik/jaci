package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

import java.util.Collections;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:23
 */
public class ShellFileSystem {
    private final ShellDirectory root;
    private final Map<String, ShellCommand> globalCommands;

    ShellFileSystem(ShellDirectory root, Map<String, ShellCommand> globalCommands) {
        this.root = root;
        this.globalCommands = globalCommands;
    }

    public ShellDirectory getRoot() {
        return root;
    }

    public Map<String, ShellCommand> getGlobalCommands() {
        return Collections.unmodifiableMap(globalCommands);
    }
}
