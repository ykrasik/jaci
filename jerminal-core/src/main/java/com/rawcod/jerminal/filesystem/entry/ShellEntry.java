package com.rawcod.jerminal.filesystem.entry;

import java.util.List;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public interface ShellEntry {
    String getName();
    String getDescription();

    boolean isDirectory();
    List<ShellEntry> getChildren();
}
