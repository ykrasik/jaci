/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.filesystem;

import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.exception.ShellException;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link ShellFileSystem} that promises to eventually contain a real implementation of a {@link ShellFileSystem}.<br>
 * Used for resolve dependency problems - since a fileSystem is immutable and requires all
 * it's commands upfront, but some commands operate on the fileSystem, creating a circular dependency.
 *
 * @author Yevgeny Krasik
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
