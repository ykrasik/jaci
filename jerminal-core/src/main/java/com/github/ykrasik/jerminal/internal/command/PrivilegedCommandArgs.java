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

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;

/**
 * A {@link CommandArgs} that provides access to more parameter types.
 * For internal use, should never be used externally.
 *
 * @author Yevgeny Krasik
 */
public interface PrivilegedCommandArgs extends CommandArgs {
    /**
     * @param name The parameter name.
     * @return The {@link InternalShellDirectory} value parsed by the parameter specified by 'name'.
     * @throws IllegalArgumentException If no value was parsed by the parameter specified by 'name'
     *                                  or if the value parsed by the parameter isn't an {@link InternalShellDirectory}.
     */
    InternalShellDirectory getDirectory(String name) throws IllegalArgumentException;

    /**
     * @return The next positional {@link InternalShellDirectory} value.
     * @throws IllegalArgumentException If there are no more positional values
     *                                  or if the next positional value isn't an {@link InternalShellDirectory}.
     */
    InternalShellDirectory popDirectory() throws IllegalArgumentException;

    /**
     * @param name The parameter name.
     * @return The {@link InternalCommand} value parsed by the parameter specified by 'name'.
     * @throws IllegalArgumentException If no value was parsed by the parameter specified by 'name'
     *                                  or of the value parsed by the parameter isn't an {@link InternalCommand}.
     */
    InternalCommand getCommand(String name) throws IllegalArgumentException;

    /**
     * @return The next positional {@link InternalCommand} value.
     * @throws IllegalArgumentException If there are no more positional values
     *                                  or if the next positional value isn't a {@link InternalCommand}.
     */
    InternalCommand popCommand() throws IllegalArgumentException;

    /**
     * @return An array of {@link Object} representing the positional parameter values.
     */
    Object[] toObjectArray();
}
