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

import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;

/**
 * An <b>immutable</b> hierarchy of {@link ShellDirectory directories} and {@link ShellCommand commands}.<br>
 *
 * @author Yevgeny Krasik
 */
public interface ShellFileSystem {
    /**
     * Returns the root {@link ShellDirectory}.
     */
    ShellDirectory getRoot();

    /**
     * Returns the current {@link ShellDirectory}.
     */
    ShellDirectory getCurrentDirectory();

    /**
     * Sets the current {@link ShellDirectory}.
     */
    void setCurrentDirectory(ShellDirectory directory);

    /**
     * Parse the given path as a path to a {@link ShellCommand}.<br>
     * Parsing a path always starts from the current directory, unless the path explicitly starts from root.
     * @throws ParseException If the path is invalid or doesn't point to a {@link ShellCommand}.
     */
    ShellCommand parsePathToCommand(String rawPath) throws ParseException;

    /**
     * Parse the given path as a path to a {@link ShellDirectory}.
     * Parsing a path always starts from the current directory, unless the path explicitly starts from root.
     * @throws ParseException If the path is invalid or doesn't point to a {@link ShellDirectory}.
     */
    ShellDirectory parsePathToDirectory(String rawPath) throws ParseException;

    /**
     * Offer auto complete suggestions for the next {@link ShellDirectory} in this path.
     * @throws ParseException If the path is invalid.
     */
    AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) throws ParseException;

    /**
     * Offer auto complete suggestions for the next {@link ShellDirectory} or {@link ShellCommand} in this path.
     * @throws ParseException If the path is invalid.
     */
    AutoCompleteReturnValue autoCompletePath(String rawPath) throws ParseException;
}
