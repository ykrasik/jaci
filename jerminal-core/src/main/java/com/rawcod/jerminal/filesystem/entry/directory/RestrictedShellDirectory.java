package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.RestrictedShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 01:21
 */
public class RestrictedShellDirectory implements ShellDirectory {
    private final ShellDirectory delegate;
    private final List<ShellEntry> restrictedChildren;

    public RestrictedShellDirectory(ShellDirectory delegate) {
        this.delegate = delegate;
        this.restrictedChildren = Collections.unmodifiableList(createRestrictedChildren(delegate));
    }

    private List<ShellEntry> createRestrictedChildren(ShellDirectory directory) {
        final List<ShellEntry> children = new ArrayList<>(directory.getChildren().size());
        for (ShellEntry entry : directory.getChildren()) {
            final ShellEntry entryToAdd;
            if (entry.isDirectory()) {
                entryToAdd = new RestrictedShellDirectory(entry.getDirectory());
            } else {
                entryToAdd = new RestrictedShellCommand(entry.getCommand());
            }
            children.add(entryToAdd);
        }
        return children;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
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
        return delegate.getCommand();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public List<ShellEntry> getChildren() {
        return restrictedChildren;
    }

    @Override
    public ShellDirectory getParent() {
        return delegate.getParent();
    }

    @Override
    public ShellDirectory addEntries(ShellEntry... entries) {
        throw restrictedException();
    }

    @Override
    public ShellDirectory addEntry(ShellEntry entry) {
        throw restrictedException();
    }

    @Override
    public DirectoryEntryManager getEntryManager() {
        throw restrictedException();
    }

    private UnsupportedOperationException restrictedException() {
        final String message = String.format("Operation not allowed on restricted directory '%s'!", getName());
        throw new UnsupportedOperationException(message);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
