package com.rawcod.jerminal.filesystem.entry.view;

import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 20:22
 */
public interface ShellEntryView {
    String getName();
    String getDescription();

    boolean isDirectory();
    List<ShellEntryView> getChildren();
}
