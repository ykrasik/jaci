package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.filesystem.entry.ShellEntry;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 14:44
 */
public interface ReadOnlyDirectory extends ShellEntry {
    boolean isEmpty();

    ReadOnlyDirectory getParent();
}
