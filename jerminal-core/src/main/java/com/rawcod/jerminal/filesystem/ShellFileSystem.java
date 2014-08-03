package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectoryImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:23
 */
public class ShellFileSystem {
    private final ShellDirectory root;
    private final Set<ShellCommand> globalCommands;

    public ShellFileSystem() {
        this.root = new ShellDirectoryImpl("", "Root");
        this.globalCommands = new HashSet<>();
    }

    public ShellDirectory getRoot() {
        return root;
    }

    public void addGlobalCommand(ShellCommand command) {
        globalCommands.add(command);
    }

    public Set<ShellCommand> getGlobalCommands() {
        return globalCommands;
    }
}
