package com.rawcod.jerminal.filesystem.entry.view;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 20:23
 */
public class ShellEntryViewImpl implements ShellEntryView {
    private final String name;
    private final String description;
    private final boolean directory;
    private final List<ShellEntryView> children;

    public ShellEntryViewImpl(String name, String description, boolean directory, List<ShellEntryView> children) {
        this.name = name;
        this.description = description;
        this.directory = directory;
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public List<ShellEntryView> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }
}
