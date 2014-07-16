package com.rawcod.jerminal.shell.entry;

import com.rawcod.jerminal.shell.ShellManager;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public interface ShellEntry {
    String getName();
    String getDescription();

    boolean isDirectory();

    /**
     * Automatically called when a command or directory is added to the shellManager.
     */
    void install(ShellManager manager);
}
