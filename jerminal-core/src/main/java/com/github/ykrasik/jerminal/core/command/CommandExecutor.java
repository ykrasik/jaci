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

package com.github.ykrasik.jerminal.core.command;

import com.github.ykrasik.jerminal.api.CommandOutput;

/**
 * Executes code according to given arguments.
 *
 * @author Yevgeny Krasik
 */
public interface CommandExecutor {
    /**
     * Executes code according to given {@link CommandArgs args}.<br>
     * Output may be written to the {@link CommandOutput output}.<br>
     * May throw any exception.
     *
     * @param output Output source.
     * @param args Parsed args, can be queried for arg values.
     * @throws Exception If an error occurs during execution.
     */
    void execute(CommandOutput output, CommandArgs args) throws Exception;
}
