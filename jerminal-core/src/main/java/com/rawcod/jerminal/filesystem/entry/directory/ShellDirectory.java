package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public class ShellDirectory extends AbstractShellEntry implements ReadOnlyDirectory {
    private static final ShellEntryComparator COMPARATOR = new ShellEntryComparator();

    private final List<ShellEntry> children;
    private final DirectoryEntryManager entryManager;

    private ShellDirectory parent;

    public ShellDirectory(String name) {
        this(name, "Directory");
    }

    public ShellDirectory(String name, String description) {
        super(name, description);

        this.children = new ArrayList<>();
        this.entryManager = new DirectoryEntryManager(this);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public List<ShellEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public ShellDirectory getParent() {
        return parent;
    }

    public ShellDirectory addEntries(ShellEntry... entries) {
        for (ShellEntry entry : entries) {
            doAddEntry(entry, false);
        }
        Collections.sort(children, COMPARATOR);
        return this;
    }

    public ShellDirectory addEntry(ShellEntry entry) {
        return doAddEntry(entry, true);
    }

    private ShellDirectory doAddEntry(ShellEntry entry, boolean sort) {
        if (entry == this) {
            throw new RuntimeException("Trying to add a directory to itself!");
        }
        if (entry.isDirectory()) {
            ((ShellDirectory) entry).setParent(this);
        }

        entryManager.addChild(entry);
        children.add(entry);
        if (sort) {
            Collections.sort(children, COMPARATOR);
        }

        return this;
    }

    private void setParent(ShellDirectory parent) {
        if (this.parent != null) {
            final String message = String.format("Directory '%s' already has a parent: '%s'", getName(), this.parent.getName());
            throw new RuntimeException(message);
        }
        this.parent = parent;
    }

    public DirectoryEntryManager getEntryManager() {
        return entryManager;
    }

    private static class ShellEntryComparator implements Comparator<ShellEntry> {
        @Override
        public int compare(ShellEntry o1, ShellEntry o2) {
            if (o1.isDirectory() && !o2.isDirectory()) {
                return 1;
            }
            if (o2.isDirectory() && !o1.isDirectory()) {
                return -1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    }
}
