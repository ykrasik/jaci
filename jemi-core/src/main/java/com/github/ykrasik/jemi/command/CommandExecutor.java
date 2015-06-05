/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.command;

import com.github.ykrasik.jemi.api.CommandOutput;

/**
 * Executes code with arguments supplied by {@link CommandArgs} and output written to {@link CommandOutput}.
 *
 * @author Yevgeny Krasik
 */
public interface CommandExecutor {
    /**
     * Executes code.
     * Arguments are supplied by the given {@link CommandArgs args}.<br>
     * Output may be written to the {@link CommandOutput output}.<br>
     * May throw any exception.
     *
     * @param output Output source.
     * @param args Parsed args, supplies argument values.
     * @throws Exception If an error occurs during execution.
     */
    void execute(CommandOutput output, CommandArgs args) throws Exception;
}
