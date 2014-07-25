package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.filesystem.entry.ShellEntry;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 14:44
 */
public interface ReadOnlyDirectory extends ShellEntry {
    boolean isEmpty();
    List<ShellEntry> getChildren();

    ReadOnlyDirectory getParent();
}
