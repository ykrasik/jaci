package com.rawcod.jerminal.filesystem.entry;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public interface ShellEntry {
    String getName();
    String getDescription();

    boolean isDirectory();
}
