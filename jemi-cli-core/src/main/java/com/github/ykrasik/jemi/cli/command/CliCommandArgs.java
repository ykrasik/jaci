/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.command;

import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.core.command.CommandArgs;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface CliCommandArgs extends CommandArgs {
    /**
     * @return The next positional {@link CliDirectory} argument.
     * @throws IllegalArgumentException If there are no more positional arguments
     *                                  or if the next positional argument isn't a {@link CliDirectory}.
     */
    CliDirectory popDirectory() throws IllegalArgumentException;

    /**
     * @return The next positional {@link CliCommand} argument.
     * @throws IllegalArgumentException If there are no more positional arguments
     *                                  or if the next positional argument isn't a {@link CliCommand}.
     */
    CliCommand popCommand() throws IllegalArgumentException;
}
