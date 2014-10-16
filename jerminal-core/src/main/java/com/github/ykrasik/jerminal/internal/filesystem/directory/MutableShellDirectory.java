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

import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;

import java.util.List;

/**
 * A mutable {@link ShellDirectory}.<br>
 * Can create child {@link ShellDirectory} when required and add {@link Command} to itself.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Use a ShellFileSystemBuilder?
public interface MutableShellDirectory extends ShellDirectory {
    /**
     * If a directory with the requested name is already a child directory of this directory, will return
     * the existing child. Otherwise, will create a new child directory with that name and return it.
     *
     * @param name Directory name.
     * @param description If a new directory needs to be created, it will be created with this description. Ignored otherwise.
     * @return An existing or newly created child {@link MutableShellDirectory}.
     */
    MutableShellDirectory getOrCreateDirectory(String name, String description);

    /**
     * Add the commands to this directory.
     *
     * @param commands Commands to add.
     * @throws com.github.ykrasik.jerminal.internal.exception.ShellException If one of the command names is invalid or a command with that name already exists under this directory.
     */
    void addCommands(Command... commands);

    /**
     * Add the commands to this directory.
     *
     * @param commands Commands to add.
     * @throws com.github.ykrasik.jerminal.internal.exception.ShellException If one of the command names is invalid or a command with that name already exists under this directory.
     */
    void addCommands(List<Command> commands);
}
