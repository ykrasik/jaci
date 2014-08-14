package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 14/08/2014
 * Time: 18:37
 *
 * A class that delegates implementation to the real fileSystem that it wraps.
 * Used for resolve dependency problems - since a fileSystem is immutable and requires all
 * it's commands upfront, but some commands operate on the fileSystem, creating a circular dependency.
 */
public class ShellFileSystemPromise implements ShellFileSystem {
    private ShellFileSystem delegate;

    public void setFileSystem(ShellFileSystem delegate) {
        if (this.delegate != null) {
            throw new ShellException("FileSystem has already been set!");
        }
        this.delegate = checkNotNull(delegate, "delegate");
    }

    @Override
    public ShellDirectory getRoot() {
        return delegate.getRoot();
    }

    @Override
    public ShellDirectory getCurrentDirectory() {
        return delegate.getCurrentDirectory();
    }

    @Override
    public void setCurrentDirectory(ShellDirectory directory) {
        delegate.setCurrentDirectory(directory);
    }

    @Override
    public ShellCommand parsePathToCommand(String rawPath) throws ParseException {
        return delegate.parsePathToCommand(rawPath);
    }

    @Override
    public ShellDirectory parsePathToDirectory(String rawPath) throws ParseException {
        return delegate.parsePathToDirectory(rawPath);
    }

    @Override
    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) throws ParseException {
        return delegate.autoCompletePathToDirectory(rawPath);
    }

    @Override
    public AutoCompleteReturnValue autoCompletePath(String rawPath) throws ParseException {
        return delegate.autoCompletePath(rawPath);
    }
}
