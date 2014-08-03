package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public class ShellDirectoryImpl extends AbstractShellEntry implements ShellDirectory {
    private static final ShellEntryComparator COMPARATOR = new ShellEntryComparator();

    private final List<ShellEntry> children;
    private final DirectoryEntryManager entryManager;

    private ShellDirectory parent;

    public ShellDirectoryImpl(String name) {
        this(name, "Directory");
    }

    public ShellDirectoryImpl(String name, String description) {
        super(name, description);

        this.children = new ArrayList<>();
        this.entryManager = new DirectoryEntryManager(this);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public ShellDirectory getDirectory() {
        return this;
    }

    @Override
    public ShellCommand getCommand() {
        final String message = String.format("'%s' is a directory, not a command!", getName());
        throw new IllegalStateException(message);
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

    @Override
    public ShellDirectory addEntries(ShellEntry... entries) {
        for (ShellEntry entry : entries) {
            doAddEntry(entry, false);
        }
        Collections.sort(children, COMPARATOR);
        return this;
    }

    @Override
    public ShellDirectory addEntry(ShellEntry entry) {
        return doAddEntry(entry, true);
    }

    private ShellDirectory doAddEntry(ShellEntry entry, boolean sort) {
        if (entry == this) {
            throw new ShellException("Trying to add a directory to itself!");
        }
        if (entry.isDirectory()) {
            ((ShellDirectoryImpl) entry).setParent(this);
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
            throw new ShellException(message);
        }
        this.parent = parent;
    }

    @Override
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
