package com.rawcod.jerminal.filesystem.entry;

import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public interface ShellEntry {
    String getName();
    String getDescription();

    boolean isDirectory();
    ShellDirectory getDirectory();
    ShellCommand getCommand();
}
