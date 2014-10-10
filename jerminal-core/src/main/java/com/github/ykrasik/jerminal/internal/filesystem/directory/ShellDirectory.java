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

package com.github.ykrasik.jerminal.internal.filesystem.directory;

import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFile;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

/**
 * A <b>immutable</b> container for {@link ShellDirectory directories} and {@link ShellFile files}.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Directories don't need a description.
public interface ShellDirectory extends ShellEntry {

    // FIXME: JavaDoc

    // FIXME: Is this needed?
    ShellDirectory addFiles(ShellFile... files);

    ShellDirectory addFiles(List<ShellFile> files);

    /**
     * @return True if this directory has no children.
     */
    boolean isEmpty();

    /**
     * @return This directory's children.
     */
    Collection<ShellEntry> getChildren();

    /**
     * @return This directory's parent.
     */
    Optional<ShellDirectory> getParent();

    /**
     * @return A copy of this directory with it's parent set to the given parent.
     */
    ShellDirectory setParent(ShellDirectory parent);

    /**
     * @return A child {@link ShellEntry} with the given name. Does not throw any exceptions.
     */
    Optional<ShellEntry> getChildDirectory(String name);

    /**
     * @return A copy of this directory with the given directory as a child.
     *         If a previous entry (child or file) existed under the same name, it will be overwritten.
     */
    ShellDirectory setChildDirectory(ShellDirectory child);

    /**
     * @return A child {@link ShellFile} with the given name, if one exists.
     *
     * @throws ParseException If no such child {@link ShellFile} exists.
     */
    ShellFile getFile(String name) throws ParseException;

    /**
     * @return A child {@link ShellDirectory} with the given name, if one exists.
     *
     * @throws ParseException If no such child {@link ShellDirectory} exists.
     */
    ShellDirectory getDirectory(String name) throws ParseException;

    /**
     * @return A {@link Trie} containing auto complete suggestions for the a child {@link ShellDirectory}
     *         that starts with the given prefix.
     *
     * @throws ParseException If this {@link ShellDirectory} is empty.
     */
    // TODO: Do I really want to throw this exception? Directories aren't supposed to be empty anyway.
    Trie<AutoCompleteType> autoCompleteDirectory(String prefix) throws ParseException;

    /**
     * @return A {@link Trie} containing auto complete suggestions for any child entry that starts with the given prefix.
     *
     * @throws ParseException If this {@link ShellDirectory} is empty.
     */
    // TODO: Do I really want to throw this exception? Directories aren't supposed to be empty anyway.
    Trie<AutoCompleteType> autoCompleteEntry(String prefix) throws ParseException;
}
